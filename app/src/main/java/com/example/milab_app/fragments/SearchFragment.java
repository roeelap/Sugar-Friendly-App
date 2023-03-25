package com.example.milab_app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.milab_app.MainActivity;
import com.example.milab_app.R;
import com.example.milab_app.objects.Dish;
import com.example.milab_app.utility.DataFetcher;
import com.example.milab_app.utility.SearchResultsRecyclerViewAdapter;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private ArrayList<Dish> allResults;
    private SearchResultsRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        EditText searchBar = rootView.findViewById(R.id.searchBar);

        // Initialize the adapter and the array list
        allResults = new ArrayList<>();

        // Initialize the search results recycler view
        RecyclerView searchResultsRecyclerView = rootView.findViewById(R.id.searchResults);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchResultsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SearchResultsRecyclerViewAdapter(getContext(), allResults);
        searchResultsRecyclerView.setAdapter(adapter);

        // Add a listener to the search bar to update the results when the text changes
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchResults(rootView, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        createSearchButtons(rootView);

        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchResults(View rootView, String query) {
        // Clear the current results
        allResults.clear();
        adapter.notifyDataSetChanged();

        if (query.isEmpty()) {
            adapter.notifyDataSetChanged();
            (rootView.findViewById(R.id.searchButtons)).setVisibility(View.VISIBLE);
            return;
        }

        // remove the buttons from the screen
        (rootView.findViewById(R.id.searchButtons)).setVisibility(View.GONE);

        // show progress bar
        ((MainActivity) requireActivity()).showProgressBar();

        // Fetch the search results
        final DataFetcher fetcher = new DataFetcher(rootView.getContext());
        fetcher.fetchSearchResults(query, response -> {
            // hide progress bar
            ((MainActivity) requireActivity()).hideProgressBar();

            if (response.isError()) {
                Log.e(TAG, "Error fetching search results");
                Toast.makeText(rootView.getContext(), "Error fetching search results", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.e(TAG, "Fetched search results successfully");
            allResults.clear();
            allResults.addAll(response.getSearchResults());

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged();
        });
    }

    private void createSearchButtons(View rootView) {
        String[] buttons = {"meat", "beef", "italian", "fish", "vegan", "dessert", "hamburger", "sugar free", "reduced sugar", "removable ingredients", "reduced carbs"};
        for (String button : buttons) {
            // create a button inside the flexbox
            Button searchButton = new Button(rootView.getContext());
            searchButton.setText(button);
            searchButton.setTextSize(12);
            // add as a child to the flexbox
            ((ViewGroup) rootView.findViewById(R.id.searchButtons)).addView(searchButton);
            // add a listener to the button
            searchButton.setOnClickListener(v -> {
                EditText searchBar = rootView.findViewById(R.id.searchBar);
                searchBar.setText(button);
            });
        }
    }
}