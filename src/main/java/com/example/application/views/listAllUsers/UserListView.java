package com.example.application.views.listAllUsers;

import com.example.application.model.User;
import com.example.application.repository.UserRepository;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "user-list", layout = MainLayout.class)
@PageTitle("User List")
public class UserListView extends VerticalLayout {

    private final UserRepository userRepository;

    @Autowired
    public UserListView(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Criação do grid para exibir os usuários
        Grid<User> userGrid = new Grid<>();
        userGrid.setItems(userRepository.findAll()); // Busca todos os usuários do banco de dados

        userGrid.addColumn(User::getFirstName).setHeader("First Name");
        userGrid.addColumn(User::getLastName).setHeader("Last Name");
        userGrid.addColumn(User::getUsername).setHeader("Username");

        // Adiciona os botões de ação usando ComponentRenderer
        userGrid.addColumn(new ComponentRenderer<>(user -> {
            Button editButton = new Button("Edit", event -> {
                openEditDialog(user);
            });
            Button deleteButton = new Button("Delete", event -> {
                openDeleteDialog(user);
            });
            deleteButton.getElement().setAttribute("theme", "error");
            
            return new HorizontalLayout(editButton, deleteButton);
        })).setHeader("Actions");

        // Adiciona o grid ao layout vertical
        add(userGrid);
    }

    private void openEditDialog(User user) {
        Dialog dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        TextField firstNameField = new TextField("First Name", user.getFirstName());
        TextField lastNameField = new TextField("Last Name", user.getLastName());
        TextField usernameField = new TextField("Username", user.getUsername());
        TextField passwordField = new TextField("Password"); // Adicione os campos necessários

        formLayout.add(firstNameField, lastNameField, usernameField, passwordField);

        Binder<User> binder = new Binder<>(User.class);
        binder.forField(firstNameField).bind(User::getFirstName, User::setFirstName);
        binder.forField(lastNameField).bind(User::getLastName, User::setLastName);
        binder.forField(usernameField).bind(User::getUsername, User::setUsername);
        binder.forField(passwordField).bind(User::getPassword, User::setPassword); // Faça o bind dos campos necessários

        binder.setBean(user);

        Button saveButton = new Button("Salvar", event -> {
            binder.validate();
            if (binder.isValid()) {
                userRepository.save(user);
                dialog.close();
                refreshGrid();
            }
        });

        dialog.add(formLayout, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(User user) {
        Dialog dialog = new Dialog();
        Span confirmMessage = new Span("Tem certeza de que deseja excluir este usuário?");
        Button confirmButton = new Button("Confirm", event -> {
            userRepository.delete(user);
            dialog.close();
            refreshGrid();
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        dialog.add(confirmMessage, new HorizontalLayout(confirmButton, cancelButton));
        dialog.open();
    }
    private void refreshGrid() {
        Grid<User> userGrid = (Grid<User>) this.getComponentAt(0);
        userGrid.setItems(userRepository.findAll());
    }
}