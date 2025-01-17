/*
 * Class Name
 *
 * Date of Initiation
 *
 * Copyright @ 2019 Team 07, CMPUT 301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at the University of Alberta.
 * You can find a copy of the license in the github wiki for this project.
 */

package vl.team07.com.virtuallibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MyBookDetailsActivity extends AppCompatActivity {
    ArrayList<Review> reviewList = new ArrayList<Review>();
    String title;
    String author;
    private String status;
    String isbn;
    String owner;
    String pickupLocation;
    String description;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private String ISBN;

    SharedPreferences preferences;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference =  database.getReference();


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.scan, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int i = item.getItemId();

        if(i == R.id.action_scan){
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    ISBN = barcode.displayValue;
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                }
            } else {
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_details);

        //test Review List
        TempList();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        title = extras.getString("TITLE");
        author = extras.getString("AUTHOR");
        isbn = extras.getString("ISBN");
        pickupLocation = extras.getString("PICKUPLOCATION");
        description = extras.getString("DESCRIPTION");
        status = extras.getString("STATUS");
        owner = extras.getString("OWNER");

//        description = "WE need a long description in here. I should just try to practice my typin " +
//                "but his should be fine. Do we need it longer? Im not too sure, but hopefully i can get" +
//                "description long enough. What if it is longer? we need to test this quick," +
//                "will this be ok?";

        setTitle(title);

        //Getting text views from activity
        final TextView bookTitleTextView = findViewById(R.id.BookTitleTextView);
        findViewById(R.id.BookTitleTextView).setSelected(true);
        final TextView authorTextView = findViewById(R.id.AuthorTextView);
        final TextView ISBNTextView = findViewById(R.id.ISBNTextView);
        final TextView DescriptionTextView = findViewById(R.id.DescriptionTextView);
        DescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        final TextView ReviewAverageScore = findViewById(R.id.AverageReviewScore);
        final TextView TopReviewer1 = findViewById(R.id.TopReviewUser1);
        final TextView TopReviewer2 = findViewById(R.id.TopReviewUser2);
        final TextView TopReviewer3 = findViewById(R.id.TopReviewUser3);
        final TextView Reviewer1Comment = findViewById(R.id.User1Comment);
        final TextView Reviewer2Comment = findViewById(R.id.User2Comment);
        final TextView Reviewer3Comment = findViewById(R.id.User3Comment);
        final TextView Reviewer1Rating = findViewById(R.id.User1Rating);
        final TextView Reviewer2Rating = findViewById(R.id.User2Rating);
        final TextView Reviewer3Rating = findViewById(R.id.User3Rating);
        final Button EditButton = findViewById(R.id.EditButton);
        final Button ViewCommentsButton = findViewById(R.id.ViewAllComments);
        final Button RequestsButton = findViewById(R.id.RequestsButton);
        final Button ConfirmReturn = findViewById(R.id.ConfirmReturn);
        final ImageView bookCover = findViewById(R.id.bookCover);
        final Button locationButton = findViewById(R.id.locationButton);

        //Loading the images from Firebase Storage
        DatabaseHandler dh = DatabaseHandler.getInstance(this);
        dh.retrieveImageFromFirebase(isbn, bookCover);


        if (status.equals("RETURNED")) {
            ConfirmReturn.setVisibility(View.VISIBLE);
            locationButton.setVisibility(View.VISIBLE);
        } else {
            ConfirmReturn.setVisibility(View.INVISIBLE);
            locationButton.setVisibility(View.INVISIBLE);
        }


        User user1 = new User("Test user1", "Test name1", "0", "Test email", 0, "Canada", 0, "");
        Book testBook = new Book(title, author, isbn, "Test user1", BookStatus.AVAILABLE, "Description", "SSN", null);
        testBook.setPickupLocation(pickupLocation);
        Review dummyReview = new Review(user1.getUserName());

        //Setting appropriate text for text views
        bookTitleTextView.setText(title);
        authorTextView.setText("by " + author);
        ISBNTextView.setText("ISBN: " + String.valueOf(isbn));
        DescriptionTextView.setText(description);
        DatabaseReference reviewReference = databaseReference.child("Reviews");
        reviewReference.child(isbn).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Review> currentReviewList = new ArrayList<Review>();
                for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                    Review review = adSnapshot.getValue(Review.class);
                    System.out.println("Comment of review is "+ review.getComment());
                    currentReviewList.add(review);
                }

                System.out.println("Size of the list in onDataChange is: " + currentReviewList.size());
                ReviewAverageScore.setText(String.valueOf(dummyReview.getAverageRating(currentReviewList)));

                if(currentReviewList.size() >= 1){
                    TopReviewer1.setText("@"+ currentReviewList.get(0).getReviewer());
                    Reviewer1Comment.setText(currentReviewList.get(0).getComment());
                    Reviewer1Rating.setText(String.valueOf(currentReviewList.get(0).getRating()));
                }

                if(currentReviewList.size() >= 2){
                    TopReviewer2.setText("@" + currentReviewList.get(1).getReviewer());
                    Reviewer2Comment.setText(currentReviewList.get(1).getComment());
                    Reviewer2Rating.setText(String.valueOf(currentReviewList.get(1).getRating()));
                }
                if(currentReviewList.size() >= 3){
                    TopReviewer3.setText("@"+ currentReviewList.get(2).getReviewer());
                    Reviewer3Comment.setText(currentReviewList.get(2).getComment());
                    Reviewer3Rating.setText(String.valueOf(currentReviewList.get(2).getRating()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EditBookDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TITLE", title);
                extras.putString("AUTHOR", author);
                extras.putString("ISBN", isbn);
//                extras.putString("PICKUPLOCATION", pickupLocation);
                extras.putString("DESCRIPTION", description);
//                extras.putString("STATUS", status);
//                extras.putString("OWNER", owner);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });

        RequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("AVAILABLE") || status.equals("REQUESTED")) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RequestActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("TITLE", title);
                    extras.putString("AUTHOR", author);
                    extras.putString("ISBN", isbn);
                    extras.putString("PICKUPLOCATION", pickupLocation);
                    extras.putString("DESCRIPTION", description);
                    extras.putString("STATUS", status);
                    extras.putString("OWNER", owner);
                    intent.putExtras(extras);
                    context.startActivity(intent);
                } else if (status.equals("BORROWED")) {
                    Context context = v.getContext();
                    CharSequence text = "Invalid! This book has been borrowed.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });



        ConfirmReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("RETURNED")) {
                    Book book = new Book(title, author, isbn, owner, BookStatus.AVAILABLE, description, "");

                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String current_userName = preferences.getString("current_userName", "n/a");

                    DatabaseHandler dh = DatabaseHandler.getInstance(getApplicationContext());
                    dh.confirmReturnedBook(book);
                    Toast.makeText(MyBookDetailsActivity.this, "Book successfully returned",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    dh.showToast("Return confirmed!");

                } else {
                    Context context = v.getContext();
                    CharSequence text = "Invalid! This book has not been returned yet.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        ViewCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, AllReviewsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TITLE", title);
                extras.putString("AUTHOR", author);
                extras.putString("ISBN", isbn);
                extras.putString("DESCRIPTION", description);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });

        locationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals("RETURNED")) {
                    DatabaseHandler dh = DatabaseHandler.getInstance(MyBookDetailsActivity.this);
                    Context context = v.getContext();
                    dh.navToPickUpLocation(testBook, context);
                } else {
                    Toast.makeText(MyBookDetailsActivity.this, "Book is not being returned",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void TempList() {
        User user1 = new User("Testusername1", "Test name1", "0", "Test email", 0, "Canada", 0, "");
        Book testBook = new Book(title, author, isbn, "Testusername1", BookStatus.AVAILABLE, "Description", "SSN", null);
        Review testReview1 = new Review(user1.getUserName());
        testReview1.setRating(4.9);
        testReview1.setComment("This is reviewer 1's comment");
        reviewList.add(testReview1);

        User user2 = new User("Testusername2", "Test name2", "0", "Test email", 0, "Canada", 0, "");
        Review testReview2 = new Review(user2.getUserName());
        testReview2.setRating(4.4);
        testReview2.setComment("This is reviewer 2's comment");
        reviewList.add(testReview2);

        User user3 = new User("Testusername3", "Test name3", "0", "Test email", 0, "Canada", 0, "");
        Review testReview3 = new Review(user3.getUserName());
        testReview3.setRating(4.7);
        testReview3.setComment("This is reviewer 3's comment");
        reviewList.add(testReview3);

    }

}