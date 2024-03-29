package com.example.application.views.form;

import com.example.application.model.User;
import com.example.application.repository.UserRepository;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Formulário")
@Route(value = "form-layout-basic", layout = MainLayout.class)
public class FormLayoutBasic extends Div {

    @Autowired
    private UserRepository userRepository;

    public FormLayoutBasic() {
        TextField firstName = new TextField("Nome");
        TextField lastName = new TextField("Sobrenome");
        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Senha");
        PasswordField confirmPassword = new PasswordField("Confirme a Senha");

        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("padding", "3em");
        formLayout.add(firstName, lastName, username, password, confirmPassword);

        Button saveButton = new Button("Salvar", event -> {
            if (password.getValue().equals(confirmPassword.getValue())) {
                User user = new User();
                user.setFirstName(firstName.getValue());
                user.setLastName(lastName.getValue());
                user.setUsername(username.getValue());
                user.setPassword(password.getValue());

                userRepository.save(user);
                Notification.show("Usuário salvo com sucesso!");

                // Limpar os campos após salvar o usuário
                firstName.clear();
                lastName.clear();
                username.clear();
                password.clear();
                confirmPassword.clear();
            } else {
                Notification.show("Senhas não coincidem!");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        formLayout.add(saveButton);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(username, 2);
        formLayout.setColspan(saveButton, 2); // Botão ocupando duas colunas
        formLayout.setColspan(password, 2); // Campo de senha ocupando duas colunas
        formLayout.setColspan(confirmPassword, 2); // Campo de confirmação de senha ocupando duas colunas

        // Adicionando margem ao botão
        saveButton.getStyle().set("margin-top", "1em");
        saveButton.getStyle().set("margin-bottom", "1em");

        add(formLayout);
    }
}
