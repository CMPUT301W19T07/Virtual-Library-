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
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;


public class AddBookFragment extends android.support.v4.app.Fragment {


    private TextView TitleView, AuthorView, ISBNView, DescriptionView;
    private EditText TitleEdit, AuthorEdit, ISBNEdit, DescriptionEdit;
    private Button addButton;
    private Book book;
    private Gson gson;

    public AddBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View AddBookView =  inflater.inflate(R.layout.fragment_add_book, container, false);

        getActivity().setTitle("Add Book");

        TitleView = (TextView) AddBookView.findViewById(R.id.addTitleView);
        AuthorView = (TextView) AddBookView.findViewById(R.id.addAuthorView);
        ISBNView = (TextView) AddBookView.findViewById(R.id.addISBNView);
        DescriptionView = (TextView) AddBookView.findViewById(R.id.addDescriptionView);

        TitleEdit = (EditText) AddBookView.findViewById(R.id.enterTitleView);
        AuthorEdit = (EditText) AddBookView.findViewById(R.id.enterAuthorView);
        ISBNEdit = (EditText) AddBookView.findViewById(R.id.enterISBNView);
        DescriptionEdit = (EditText) AddBookView.findViewById(R.id.enterDescriptionView);

        addButton = (Button) AddBookView.findViewById(R.id.addButton);

        return AddBookView;
    }

    public void onStart(){
        super.onStart();


        // Put book to 'My Books' Fragment by click ADD button
        // Will be update use firebase later
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title, author, description;
                int ISBN;
                // Set new book

                title = TitleEdit.getText().toString();
                author = AuthorEdit.getText().toString();
                description = DescriptionEdit.getText().toString();

                try {
                    ISBN = Integer.parseInt(ISBNEdit.getText().toString());
                }catch (NumberFormatException e){
                    ISBN = 0;
                }

                book = new Book();
                book.setTitle(title);
                book.setAuthor(author);
                book.setDescription(description);
                book.setISBN(ISBN);

                String SearchStringName = book.getTitle()+"m"+book.getAuthor()+
                        "m"+String.valueOf(book.getISBN())+"m"+book.getDescription();

                book.setSearchString(SearchStringName);

                DatabaseHandler dh = new DatabaseHandler(getActivity());
                dh.addBook(book);

            }
        });


    }

}