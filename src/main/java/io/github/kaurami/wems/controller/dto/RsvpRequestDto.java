package io.github.kaurami.wems.controller.dto;


import io.github.kaurami.wems.model.TransferOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class RsvpRequestDto {

    @NotBlank(message = "Контактный телефон обязателен")
    @Pattern(regexp = "\\+?[0-9\\s\\(\\)-]{10,18}", message = "Неверный формат телефона")
    private String contactPhone;

    @NotEmpty(message = "Должен быть хотя бы один гость")
    @Valid
    private List<GuestDto> guests;

    @NotNull(message = "Пожалуйста, выберите опцию трансфера")
    private TransferOption transferOption;


    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public List<GuestDto> getGuests() {
        return guests;
    }

    public void setGuests(List<GuestDto> guests) {
        this.guests = guests;
    }

    public TransferOption getTransferOption() {
        return transferOption;
    }

    public void setTransferOption(TransferOption transferOption) {
        this.transferOption = transferOption;
    }
}