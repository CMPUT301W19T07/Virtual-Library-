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

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NonOwnerBookDetailsActivity extends AppCompatActivity {

    ArrayList<Review> reviewList = new ArrayList<Review>();
    String title;
    String author;
    String isbn;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference =  database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_owner_book_details);

        //test Review List
        TempList();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String title = extras.getString("TITLE");
        String author = extras.getString("AUTHOR");
        String isbn = extras.getString("ISBN");
        String ownerAddress = extras.getString("OWNERADDRESS");
        String description = extras.getString("DESCRIPTION");

//        description = "WE need a long description in here. I should just try to practice my typin " +
//                "but his should be fine. Do we need it longer? Im not too sure, but hopefully i can get" +
//                "description long enough. What if it is longer? we need to test this quick," +
//                "will this be ok?";


        setTitle(title);
        //Getting text views from activity
        final TextView bookTitleTextView = findViewById(R.id.BookTitleTextView);
        final TextView authorTextView = findViewById(R.id.AuthorTextView);
        final TextView ISBNTextView = findViewById(R.id.ISBNTextView);
        final TextView DescriptionTextView = findViewById(R.id.DescriptionTextView);
        DescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        final TextView OwnerAddressTextView = findViewById(R.id.OwnerAddressTextView);
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
        final Button RequestButton = findViewById(R.id.RequestButton);
        final Button ViewCommentsButton = findViewById(R.id.ViewAllComments);
        final Button AddReviewButton = findViewById(R.id.AddReviewButton);


        User user1 = new User("Test user1", "Test name1", "0", "Test email", 0, "Canada", 0, "");
        Book testBook = new Book(title, author, isbn, "Test user1", BookStatus.AVAILABLE, "Description","SSN",null);
        Review dummyReview = new Review(user1.getUserName());

        //Setting appropriate text for text views
        bookTitleTextView.setText(title);
        authorTextView.setText("by " + author);
        ISBNTextView.setText("ISBN: "  +String.valueOf(isbn));
        DescriptionTextView.setText(description);
        OwnerAddressTextView.setText(ownerAddress);

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



        RequestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                CharSequence text = "Request Sent";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

        ViewCommentsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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

        AddReviewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context context = v.getContext();
                Intent intent = new Intent(context, AddReviewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TITLE", title);
                extras.putString("AUTHOR", author);
                extras.putString("ISBN", isbn);
                extras.putString("DESCRIPTION", description);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });


    }
    public void TempList(){
        User user1 = new User("Testusername1", "Test name1", "0", "Test email", 0, "Canada", 0, "");
        Book testBook = new Book(title, author, isbn, "Testusername1", BookStatus.AVAILABLE, "Description","SSN",null);
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
