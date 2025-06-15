package io.github.kurasey.wedding_invitation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GuestDto {

    @NotBlank(message = "Имя гостя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя гостя должно содержать от 2 до 100 символов")
    private String name;

    private List<String> drinks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<String> drinks) {
        this.drinks = drinks;
    }
}
