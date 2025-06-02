package io.github.kurasey.wedding_invitation.service;

import io.github.kurasey.wedding_invitation.model.Beverage;
import io.github.kurasey.wedding_invitation.model.Guest;
import io.github.kurasey.wedding_invitation.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public List<Guest> findAll(){
        return guestRepository.findAll();
    }

    public Guest save(Guest guest) {
        return guestRepository.save(guest);
    }

    public List<Guest> findGuestByBeverage(Beverage beverage) {
        return guestRepository.findByBeveragesContaining(beverage);
    }


}
