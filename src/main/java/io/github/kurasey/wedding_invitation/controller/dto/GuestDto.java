package io.github.kurasey.wedding_invitation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GuestDto {

    private Long id;

    @NotBlank(message = "Имя гостя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя гостя должно содержать от 2 до 100 символов")
    private String name;
    private boolean willAttend;
    private List<String> drinks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWillAttend() {
        return willAttend;
    }

    public void setWillAttend(boolean willAttend) {
        this.willAttend = willAttend;
    }

    public List<String> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<String> drinks) {
        this.drinks = drinks;
    }
}
