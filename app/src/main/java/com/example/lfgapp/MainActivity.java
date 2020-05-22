package com.example.lfgapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private User FireStoreUser;
    private Game CurrentGame;
    private ListenerRegistration GameRef;
    private ListenerRegistration UserRef;
    private ListenerRegistration UsersRef;
    private ArrayList<User> users;
    private Button RemoveFriend;
    private Button PlayButton;
    private Button JoinButton;
    private EditText emailedittext;
    private Button SignOut;
    private Button AddFriend;
    private View GameView;
    private View ProfileView;
    private static final String TAG = "GetUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();

        //start my stuff below

        ProfileView = findViewById(R.id.profile_view);
        ProfileView.setVisibility(View.GONE);
        GameView = findViewById(R.id.game_view);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getUser();
        updateMenu(menu);
        emailedittext = ((EditText) findViewById(R.id.Email_text));
        PlayButton = ((Button) findViewById(R.id.play_button));
        JoinButton = ((Button) findViewById(R.id.join_button));
        SignOut = ((Button) findViewById(R.id.sign_out_button));
        AddFriend = ((Button) findViewById(R.id.add_friend));
        RemoveFriend = ((Button) findViewById(R.id.remove_friend));
        View header = navigationView.getHeaderView(0);
        ImageView img = (ImageView) header.findViewById(R.id.pfp);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView.setVisibility(View.GONE);
                ProfileView.setVisibility(View.VISIBLE);
                UpdateProfile();
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT);
            }
        });

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().addIdTokenListener(new FirebaseAuth.IdTokenListener() {
                    @Override
                    public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                        logout();

                    }
                });

            }
        });

        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (PlayButton.getText().toString().equals("Play")) {

                    db.collection("games").document(CurrentGame.getName()).update("playing", FieldValue.arrayUnion(FireStoreUser.toMap()));

                    PlayButton.setText("Stop");
                } else if (PlayButton.getText().toString().equals("Stop")) {
                    if (FireStoreUser.in(CurrentGame.getPlaying())) {
                        db.collection("games").document(CurrentGame.getName()).update("playing", FieldValue.arrayRemove(FireStoreUser.toMap()));

                    }
                    PlayButton.setText("Play");
                }
            }
        });

        JoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (JoinButton.getText().toString().equals("Join")) {

                    db.collection("games").document(CurrentGame.getName()).update("looking", FieldValue.arrayUnion(FireStoreUser.toMap()));

                    JoinButton.setText("Leave");
                } else if (JoinButton.getText().toString().equals("Leave")) {
                    if (FireStoreUser.in(CurrentGame.getLooking())) {
                        Log.d("REMOVE", FireStoreUser.toMap().toString());
                        db.collection("games").document(CurrentGame.getName()).update("looking", FieldValue.arrayRemove(FireStoreUser.toMap()));

                    }
                    JoinButton.setText("Join");
                }
            }
        });

        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "task successful");
                            users = new ArrayList<User>();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            Log.d("SIZE", String.valueOf(documents.size()));
                            for (DocumentSnapshot document : documents) {
                                users.add(document.toObject(User.class));

                            }

                            for (User user : users) {
                                if (user.getEmail().equals(emailedittext.getText().toString())) {
                                    db.collection("users").document(FireStoreUser.getUID()).update("friends", FieldValue.arrayUnion(user.getUID())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                UpdateProfile();
                                            }
                                        }
                                    });

                                }
                            }


                        } else {
                            Log.d("ERROR", task.getException().toString());
                        }
                    }
                });
            }
        });


        RemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","Beginning");
                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "task successful");
                            users = new ArrayList<User>();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                users.add(document.toObject(User.class));

                            }

                            for (User user : users) {
                                Log.d("TAG","HERE");
                                if (user.getEmail().equals(emailedittext.getText().toString())) {
                                    db.collection("users").document(FireStoreUser.getUID()).update("friends", FieldValue.arrayRemove(user.getUID())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                UpdateProfile();
                                            }
                                        }
                                    });
                                }
                            }


                        } else {
                            Log.d("ERROR", task.getException().toString());
                        }
                    }
                });
            }
        });
    }









    private void logout() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

    }


    private void UpdateProfile() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("TAG","task successful");
                    users = new ArrayList<User>();
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    Log.d("SIZE", String.valueOf(documents.size()));
                    for(DocumentSnapshot document: documents){
                        users.add(document.toObject(User.class));

                    }

                    final ListView friendlist = (ListView)findViewById(R.id.friend_list);
                    friendlist.setAdapter(getFriendAdapter());


                }
                else{
                    Log.d("ERROR",task.getException().toString());
                }
            }
        });

    }

    private ArrayAdapter<String> getFriendAdapter(){
        ArrayList<String> friends = new ArrayList<>();
        Log.d("USERS",users.toString());
        for(String friend:FireStoreUser.getFriends()){
            for(User user: users){
                Log.d("TAG",user.getUID());
                Log.d("TAG",friend);
                if(user.getUID().equals(friend)){
                    friends.add(user.getGamerTag()+"      " +user.getEmail());
                }
            }
        }
        return new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,friends);
    }

    private void updateMenu(final Menu menu) {
        menu.clear();
        db.collection("games").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> result = task.getResult().getDocuments();
                    for(DocumentSnapshot document:result){
                        menu.add(document.getId()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                GameView.setVisibility(View.VISIBLE);
                                ProfileView.setVisibility(View.GONE);
                                UpdateGameRef(item.toString());
                                ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT);
                                updateMenu(menu);
                                return true;
                            }
                        });
                    }
                }
            }
        });
    }


    private void UpdateGameRef(String game){
        if(GameRef!=null){
            Log.d("TAG","remove ref");
            GameRef.remove();
        }
            GameRef=db.collection("games").document(game).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d("TAG",e.toString());
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        CurrentGame = documentSnapshot.toObject(Game.class);
                        layoutChanges();
                    } else {
                        Log.d("TAG","data null");

                    }

                }
            });
        }

    private void layoutChanges() {
        ((TextView)findViewById(R.id.game_title)).setText(CurrentGame.getName());
        ((ListView)findViewById(R.id.interested_list)).setAdapter(getAdapter(CurrentGame.getLooking()));
        ((ListView)findViewById(R.id.playing_list)).setAdapter(getAdapter(CurrentGame.getPlaying()));
        if(FireStoreUser.in(CurrentGame.getLooking())){
            ((Button)findViewById(R.id.join_button)).setText("Leave");
        }
        else{
            ((Button)findViewById(R.id.join_button)).setText("Join");
        }
        if(FireStoreUser.in(CurrentGame.getPlaying())){
            ((Button)findViewById(R.id.play_button)).setText("Stop");
        }
        else{
            ((Button)findViewById(R.id.play_button)).setText("Play");
        }

    }

    public void getUser(){
        Log.d("UID",firebaseUser.getUid());
        db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot Document = task.getResult();
                    if(Document.exists()){
                        FireStoreUser = Document.toObject(User.class);
                        Log.d("TAG",FireStoreUser.getEmail());
                        UpdateGameRef("Rainbow 6");
                        ((TextView)findViewById(R.id.username_header)).setText(FireStoreUser.getGamerTag());
                        ((TextView)findViewById(R.id.email_header)).setText(FireStoreUser.getEmail());
                        ((TextView)findViewById(R.id.username_profile_text)).setText(FireStoreUser.getGamerTag());
                        ((TextView)findViewById(R.id.email_profile_text)).setText(FireStoreUser.getEmail());
                        UserRef = db.collection("users").document(FireStoreUser.getUID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.d("TAG",e.toString());
                                    return;
                                }
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    FireStoreUser = documentSnapshot.toObject(User.class);
                                } else {
                                    Log.d("TAG","data null");

                                }
                            }
                        });
                    }
                    else{
                        Log.d("TAG","No Document");
                    }

                }
                else{
                    Log.d("ERROR",task.getException().toString());
                }


            }
        });
    }
    public ArrayAdapter<String> getAdapter(ArrayList<SimpleUser> players){
        if(FireStoreUser!=null){
            Log.d("TAG",FireStoreUser.getEmail());
        }
        ArrayList<String> temp = FireStoreUser.filterFriendsString(players);
        if(temp.isEmpty()){
           temp = new ArrayList<>();
           temp.add("Empty");
        }
        Log.d("TAG",temp.toString());
            return new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,temp);
        }



}
