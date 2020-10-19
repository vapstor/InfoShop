package br.com.infoshop.repository;

import android.util.Log;

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
public class FirebaseRepository {

    public final FirebaseAuth auth;
    public final FirebaseFirestore database;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        user.setUid(uid);
                        if (authResult != null && authResult.getAdditionalUserInfo() != null) {
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                user.setNew(true);
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
                                                                        user.setCreated(true);
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
                                user.setNew(false);
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

    public MutableLiveData<FirebaseUser> firebaseLogin(String username, String password) {
        MutableLiveData<FirebaseUser> userMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            auth.signInWithEmailAndPassword(username, password).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userMutableLiveData.setValue(auth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            userMutableLiveData.setValue(null);
                        }
                    });
        } else {
            userMutableLiveData.setValue(firebaseUser);
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
            return this.database.collection("projetos").limit(pageLimit).get();
        }
        Query query = this.database.collection("projetos").orderBy("id").startAfter(lastIdFetched).limit(pageLimit);
        return query.get();
    }

    @Singleton
    public Task<QuerySnapshot> fetchFavorites() {
        return this.database.collection("favoritos").get();
    }

    @Singleton
    public Task<Void> addToFavorites(Project project) {
        return this.database.collection("favoritos").document("projeto_" + project.getId()).set(project);
    }

    @Singleton
    public Task<Void> removeFromFavorites(Project project) {
        return this.database.collection("favoritos").document("projeto_" + project.getId()).delete();
    }
}
