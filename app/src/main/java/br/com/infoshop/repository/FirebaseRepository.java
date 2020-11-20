package br.com.infoshop.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import br.com.infoshop.model.Project;
import br.com.infoshop.model.User;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;
import static br.com.infoshop.utils.Constants.USERS;

/**
 * @author vapstor - 18/10/2020
 * <p>
 * Classe seguindo às boas práticas recomendada pela google, onde existe um repositório central
 * e nesse é definido se o app irá se conectar a um serviço para resgate de dados da web (API) ou ao ROOM.
 * (ROOM não implementado)
 * <p>
 * É possível incrementar a leitura em tempo real.
 * Mas como isso pode impactar no consumo dos recursos gratuitos do firebase, deixo essa decisão de projeto pra você ok?
 * A implementação é bastante intuitiva.
 * <p>
 * util:
 * https://firebase.google.com/docs/firestore/query-data/listen
 * https://firebase.google.com/docs/firestore/pricing#sao-paulo
 */


@Module
@InstallIn(ApplicationComponent.class)
public class FirebaseRepository implements FirebaseAuth.AuthStateListener {

    public final FirebaseAuth auth;
    public final FirebaseFirestore database;

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);

    @Inject
    public FirebaseRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.auth.useAppLanguage();
        this.database = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<User> firebaseSignUp(User user) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        //cria ou resgata usuario firebase
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPass()).
                addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        user.setUid(uid);
                        if (authResult != null && authResult.getAdditionalUserInfo() != null) {
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                user.setNew("true");
                                //Atualiza firebaseUser com infos do nosso Usuario
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.getName())
                                        //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                        .build();

                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                //Cria usuario no banco de dados
                                                DocumentReference uidRef = usersRef.document(user.uid);
                                                uidRef.get().addOnCompleteListener(uidTask -> {
                                                    if (uidTask.isSuccessful()) {
                                                        DocumentSnapshot document = uidTask.getResult();
                                                        if (document != null) {
                                                            if (!document.exists()) {
                                                                uidRef.set(user).addOnCompleteListener(userCreationTask -> {
                                                                    if (userCreationTask.isSuccessful()) {
                                                                        user.setCreated("true");
                                                                        authenticatedUserMutableLiveData.setValue(user);
                                                                    } else {
                                                                        Log.d(MY_LOG_TAG, userCreationTask.getException().getMessage());
                                                                    }
                                                                });
                                                            } else {
                                                                authenticatedUserMutableLiveData.setValue(user);
                                                            }
                                                        }
                                                    } else {
                                                        Log.d(MY_LOG_TAG, uidTask.getException().getMessage());
                                                    }

                                                });
                                            }
                                        });
                            } else {
                                user.setNew("false");
                                authenticatedUserMutableLiveData.setValue(user);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(MY_LOG_TAG, e.getLocalizedMessage());
                    authenticatedUserMutableLiveData.setValue(user);
                });
        return authenticatedUserMutableLiveData;
    }

    @Singleton
    public Task<DocumentSnapshot> fetchUserInfos(String uid) {
        //Resgata usuario no banco de dados
        return usersRef.document(uid).get();
    }

    public MutableLiveData<User> firebaseLogin(String username, String password) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        FirebaseUser fbUser = auth.getCurrentUser();
        if (fbUser == null) {
            if (!username.isEmpty() && !password.isEmpty()) {
                auth.signInWithEmailAndPassword(username, password).
                        addOnCompleteListener(taskLogin -> {
                            if (taskLogin.isSuccessful()) {
                                try {
                                    //auth.getCurrentUser não é mais o mesmo que fbUser
                                    fetchUserInfos(Objects.requireNonNull(auth.getCurrentUser()).getUid()).addOnCompleteListener(taskGetInfo -> {
                                        if (taskGetInfo.isSuccessful()) {
                                            DocumentSnapshot doc = taskGetInfo.getResult();
                                            if (doc != null) {
                                                User user = doc.toObject(User.class);
                                                userMutableLiveData.setValue(user);
                                            }
                                        } else {
                                            // get info fails
                                            userMutableLiveData.setValue(null);
                                        }
                                    });
                                } catch (NullPointerException e) {
                                    Log.e(MY_LOG_TAG, e.getMessage());
                                }
                            } else {
                                // sign in fails
                                userMutableLiveData.setValue(null);
                            }
                        });
            }
        } else {
            //usuario já se logou, só recuperar informações
            fetchUserInfos(fbUser.getUid()).addOnCompleteListener(taskGetInfo -> {
                if (taskGetInfo.isSuccessful()) {
                    DocumentSnapshot doc = taskGetInfo.getResult();
                    if (doc != null) {
                        User user = doc.toObject(User.class);
                        userMutableLiveData.setValue(user);
                    }
                } else {
                    userMutableLiveData.setValue(null);
                }
            });
        }
        return userMutableLiveData;
    }

    @Singleton
    public Task<QuerySnapshot> fetchProjectsCategories() {
        return this.database.collection("projetos_categoria").get();
    }

    @Singleton
    public Task<QuerySnapshot> fetchProjects(int pageLimit, int lastIdFetched) {
        if (lastIdFetched == 0) {
            return this.database.collection("projetos").orderBy("id").limit(pageLimit).get();
        }
        Query query = this.database.collection("projetos").orderBy("id").startAfter(lastIdFetched).limit(pageLimit);
        return query.get();
    }

    @Singleton
    public Task<QuerySnapshot> fetchProjectsByCategory(int pageLimit, int lastIdFetched, int idCategory) {
        if (lastIdFetched == 0) {
            return this.database.collection("projetos").whereEqualTo("id_categoria", idCategory).orderBy("id").limit(pageLimit).get();
        }
        Query query = this.database.collection("projetos").whereEqualTo("id_categoria", idCategory).orderBy("id").startAfter(lastIdFetched).limit(pageLimit);
        return query.get();
    }

    @Singleton
    public Task<QuerySnapshot> fetchProjectsByTitle(String query, int pageLimit, int lastIdFetched) {
        if (lastIdFetched == 0) {
            return this.database.collection("projetos").whereArrayContains("titulo", query).limit(pageLimit).get();
        }
        Query titleQuery = this.database.collection("projetos").whereArrayContains("titulo", query).startAfter(lastIdFetched).limit(pageLimit);
        return titleQuery.get();
    }

    @Singleton
    public Task<QuerySnapshot> fetchProjectsByDesc(String query, int pageLimit, int lastIdFetched) {
        if (lastIdFetched == 0) {
            return this.database.collection("projetos").limit(pageLimit).whereArrayContains("descricao", query).get();
        }
        Query descQuery = this.database.collection("projetos").whereArrayContains("descricao", query);
        return descQuery.get();
    }

    @Singleton
    public Task<QuerySnapshot> fetchFavorites() {
        String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        return this.database.collection("favoritos").document(userEmail).collection("my_favs").get();
    }

    @Singleton
    public Task<Void> addToFavorites(Project project) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return this.database.collection("favoritos").document(userEmail).collection("my_favs").document("projeto_" + project.getId()).set(project);
    }

    @Singleton
    public Task<Void> removeFromFavorites(Project project) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return this.database.collection("favoritos").document(userEmail).collection("my_favs").document("projeto_" + project.getId()).delete();
    }

    @Singleton
    public Task<Void> removeFromAllProjects(Project project) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return this.database.collection("projetos").document("projeto_" + project.getId()).delete();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        int i = 0;
        Log.d(MY_LOG_TAG, "A");
    }
}
