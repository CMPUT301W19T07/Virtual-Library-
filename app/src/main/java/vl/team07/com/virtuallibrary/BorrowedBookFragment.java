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
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class BorrowedBookFragment extends android.support.v4.app.Fragment {


    private RecyclerView recyclerView;
    private BookRecyclerViewAdapter adapter;
    private ArrayList<Book> borrowedBookList;

    SharedPreferences preferences;
    private DatabaseHandler databaseHandler;


    public BorrowedBookFragment() {
        // Required empty public constructor
    }


    /**
     * Create BorrowedBookFragment,
     * retrieve all book borrowed by user from firebase when fragment is created*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View BorrowedBookView = inflater.inflate(R.layout.fragment_borrowed_book, container, false);

        recyclerView = (RecyclerView) BorrowedBookView.findViewById(R.id.BorrowedBookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        borrowedBookList = new ArrayList<>();
        adapter = new BookRecyclerViewAdapter(getContext(), borrowedBookList);
        recyclerView.setAdapter(adapter);



        preferences = PreferenceManager.getDefaultSharedPreferences(BorrowedBookView.getContext());
        String current_userName = preferences.getString("current_userName", "n/a");

        databaseHandler = DatabaseHandler.getInstance(getActivity());
        databaseHandler.displayBorrowedBooks(current_userName, adapter, borrowedBookList);

        SwipeRefreshLayout pullToRefresh = BorrowedBookView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                borrowedBookList.clear();
                adapter.notifyDataSetChanged();
                databaseHandler.displayBorrowedBooks(current_userName, adapter, borrowedBookList);
                pullToRefresh.setRefreshing(false);
            }
        });


        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.indexOfChild(v);
                System.out.println("POSITION: "+position);

                Book clickedBook = borrowedBookList.get(position);

                Context context = v.getContext();
                Intent intent = new Intent(context, BorrowedBookDetailsActivity.class);
                String title = clickedBook.getTitle();
                String author = clickedBook.getAuthor();
                String isbn = clickedBook.getISBN();
                String pickUpLocation = clickedBook.getPickupLocation();
                String description = clickedBook.getDescription();
                String owner = clickedBook.getOwner();

                Bundle extras = new Bundle();
                extras.putString("TITLE", title);
                extras.putString("AUTHOR", author);
                extras.putString("ISBN", isbn);
                extras.putString("PICKUPLOCATION", pickUpLocation);
                extras.putString("DESCRIPTION", description);
                extras.putString("OWNER", owner);
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });


        return BorrowedBookView;
    }


}