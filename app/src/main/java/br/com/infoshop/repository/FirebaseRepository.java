package br.com.infoshop.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.infoshop.model.User;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;
import static br.com.infoshop.utils.Constants.USERS;

public class FirebaseRepository {

    public final FirebaseAuth auth;
    public final FirebaseFirestore database;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);


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
        if (auth.getCurrentUser() != null) {
            auth.signInWithEmailAndPassword(username, password).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            userMutableLiveData.setValue(firebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            userMutableLiveData.setValue(null);
                        }
                    });
        }
        ;
        return userMutableLiveData;
    }
}
