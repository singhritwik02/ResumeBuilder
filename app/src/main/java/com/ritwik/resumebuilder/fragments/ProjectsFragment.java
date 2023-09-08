package com.ritwik.resumebuilder.fragments;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ritwik.resumebuilder.databases.ProjectsDatabase;
import com.ritwik.resumebuilder.R;
import com.ritwik.resumebuilder.databinding.FragmentProjectsBinding;
import com.ritwik.resumebuilder.databinding.PopupAddProjectBinding;
import com.ritwik.resumebuilder.datamodel.ProjectsData;

import java.util.ArrayList;


public class ProjectsFragment extends Fragment {
    private ProjectsDatabase projectsDatabase;
    private FragmentProjectsBinding binding;
    private ArrayList<ProjectsData> projectDataArrayList = new ArrayList();

    private ProjectsViewAdapter projectsAdapter;
    private RecyclerView projectsRecyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProjectsBinding.inflate(getLayoutInflater());
        projectsRecyclerView = binding.recyclerView;
        handleColors();
        projectsAdapter = new ProjectsViewAdapter();
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectsRecyclerView.setAdapter(projectsAdapter);
        MobileAds.initialize(requireContext());
        AdView adView = binding.fpBottomBanner;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        binding.rvAddButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addClicked();
                    }
                }
        );
        updateAdapter();
        return binding.getRoot();
    }

    protected void addClicked() {
//        Intent intent = EditActivity.getProjectIntent(getContext());
//        startActivityForResult(intent, REQUEST_ADD);
        // Ritwik Singh Code
        showAddProjectPopup();
    }

    private void updateAdapter() {
        if (projectsDatabase == null) {
            projectsDatabase = new ProjectsDatabase(getContext());

        }
        projectDataArrayList.clear();
        projectsAdapter.notifyDataSetChanged();
        projectDataArrayList.addAll(projectsDatabase.getProjectsList());
        if (projectDataArrayList.size() < 1) {
            // show empty view
            binding.fpLongPressLabel.setVisibility(View.GONE);
            binding.empty.setVisibility(View.VISIBLE);
            projectsAdapter.notifyDataSetChanged();
        } else {
            // hide empty view
            binding.fpLongPressLabel.setVisibility(View.VISIBLE);
            binding.empty.setVisibility(View.GONE);
            projectsAdapter.notifyDataSetChanged();
        }
    }


    protected void handleColors() {
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.darkYellow));
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.white));


    }




    private void showAddProjectPopup() {
        PopupAddProjectBinding popupBinding = PopupAddProjectBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setElevation(100f);
        popupBinding.getRoot().post(
                new Runnable() {
                    @Override
                    public void run() {
                        //popupBinding.getRoot().getLayoutAnimation().start();
                    }
                }
        );
        popupWindow.showAtLocation(popupBinding.getRoot(), Gravity.CENTER, 0, 0);
        popupBinding.papSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String projectTitle = popupBinding.papProjectTitle.getText().toString();
                String projectDetails = popupBinding.papProjectDetails.getText().toString();
                String projectDescription = popupBinding.papProjectDescription.getText().toString();
                if (projectTitle.equals("")) {
                    Toast.makeText(getContext(), "Enter Title!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (projectDetails.equals("")) {
                    Toast.makeText(getContext(), "Enter Project Details!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (projectDescription.equals("")) {
                    Toast.makeText(getContext(), "Enter Project Description!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (projectsDatabase == null) {
                    projectsDatabase = new ProjectsDatabase(getContext());
                }
                projectsDatabase.insertData(projectTitle, projectDetails, projectDescription);
                // update adapter
                updateAdapter();
                // notifyDataChanged();
                popupWindow.dismiss();


            }
        });
    }
    // create update projects popup
    private void showUpdateProject(int position) {
        PopupAddProjectBinding popupBinding = PopupAddProjectBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot(), WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setElevation(100f);
//        filling up with current data
        popupBinding.papProjectTitle.setText(projectDataArrayList.get(position).getProjectName());
        popupBinding.papProjectDetails.setText(projectDataArrayList.get(position).getProjectDetails());
        popupBinding.papProjectDescription.setText(projectDataArrayList.get(position).getProjectDescription());

        popupWindow.showAtLocation(popupBinding.getRoot(), Gravity.CENTER, 0, 0);
        popupBinding.papSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String projectTitle = popupBinding.papProjectTitle.getText().toString();
                String projectDetails = popupBinding.papProjectDetails.getText().toString();
                String projectDescription = popupBinding.papProjectDescription.getText().toString();
                if (projectTitle.equals("")) {
                    Toast.makeText(getContext(), "Enter Title!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (projectDetails.equals("")) {
                    Toast.makeText(getContext(), "Enter Project Details!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (projectDescription.equals("")) {
                    Toast.makeText(getContext(), "Enter Project Description!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (projectsDatabase == null) {
                    projectsDatabase = new ProjectsDatabase(getContext());
                }
                projectsDatabase.insertData(projectTitle, projectDetails, projectDescription);
                // update adapter
                updateAdapter();
                // notifyDataChanged();
                popupWindow.dismiss();


            }
        });
    }

    public class ProjectsViewHolder extends RecyclerView.ViewHolder {
        public ProjectsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public TextView projectTitle = itemView.findViewById(R.id.spi_ProjectTitle);
        public TextView projectDescription = itemView.findViewById(R.id.spi_ProjectDescription);
        public TextView projectDetails = itemView.findViewById(R.id.spi_ProjectDetails);


    }

    public class ProjectsViewAdapter extends RecyclerView.Adapter<ProjectsViewHolder> {

        @NonNull
        @Override
        public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.single_project_item, parent, false);
            return new ProjectsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProjectsViewHolder holder, int position) {
            holder.projectTitle.setText("Project Title :- " + projectDataArrayList.get(position).getProjectName());
            holder.projectDescription.setText(projectDataArrayList.get(position).getProjectDescription());
            holder.projectDetails.setText(projectDataArrayList.get(position).getProjectDetails());
            // setting on click listener
            holder.itemView.setOnClickListener(
                    new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View view) {
                            showUpdateProject(position);
                        }
                    }
            );
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    DeleteConfirmation conf = new DeleteConfirmation();
                    conf.showWindow(position);

                    return false;
                }


            });

        }

        @Override
        public int getItemCount() {
            return projectDataArrayList.size();
        }
    }
    public void deleteItem(int position)
    {
        if (projectsDatabase == null) {
            projectsDatabase = new ProjectsDatabase(getContext());

        }
        if (projectsDatabase.deleteData(projectDataArrayList.get(position).getProjectName())) {
            Toast.makeText(getContext(), "Delete Success!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Delete Failed!!", Toast.LENGTH_SHORT).show();
        }
        updateAdapter();
    }
    private class DeleteConfirmation{
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_delete_confirmation,null,false);
        PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        public void showWindow(int position){
            window.setElevation(100f);
            window.showAtLocation(view,Gravity.CENTER,0,0);
            TextView cancel,delete,title;
            cancel = view.findViewById(R.id.pdc_CancelButton);
            delete = view.findViewById(R.id.pdc_DeleteButton);
            title = view.findViewById(R.id.pdc_ConfirmationText);
            title.setText("Sure to delete "+projectDataArrayList.get(position).getProjectName()+" ?");
            cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // dismiss
                    window.dismiss();

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    deleteItem(position);
                    window.dismiss();
                }
            });
        }

    }


}

