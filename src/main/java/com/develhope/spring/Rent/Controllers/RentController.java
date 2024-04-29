package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Rent.Entities.DTO.ModifyRentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.Rent;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Rent.Serivices.RentService;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Entities.UserTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentService rentService;

    // inserisci un nuovo noleggio
    @PostMapping("/create/{userId}")
    public ResponseEntity<Rent> createRent(@PathVariable Long userId, @RequestBody RentDTO rentDTO) {

        User user = userRepository.findById(userId).get();

        if (user.getUserType() == UserTypes.ADMIN ||user.getUserType() == UserTypes.SELLER) {
            Rent rent = rentService.createRent(rentDTO);
            if (rent == null) {
                // se il noleggio e' null, errore
                return ResponseEntity.badRequest().build();
            }
            return new ResponseEntity<>(rent, HttpStatus.CREATED);
        } return ResponseEntity.badRequest().build();
    }

    // ottieni la lista dei noleggi
    @GetMapping("/list{userId}")
    public ResponseEntity<List<Rent>> rentList(@PathVariable Long userId) {

        User user = userRepository.findById(userId).get();

        List<Rent> rentals = user.getOrders();
        // Restituisci la lista dei noleggi messaggio ok
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }

    // ottieni un noleggio da id
    @GetMapping("/search/{userId}/{rentId}")
    public ResponseEntity<Rent> getRentById(@PathVariable Long id) {
        Rent rent = rentService.getRentById(id);
        if (rent == null) {
            // se il noleggio e' null, errore
            return ResponseEntity.notFound().build();
        }
        // restituisci il noleggio trovato con messaggio ok
        return new ResponseEntity<>(rent, HttpStatus.OK);
    }

    // modifica le date di un noleggio
    @PutMapping("/update/{userId}/{rentId}")
    public ResponseEntity<Rent> updateRentDates(@PathVariable Long id, @RequestBody ModifyRentDTO modifyRentDTO) {
        Rent updatedRent = rentService.updateRentDates(id, modifyRentDTO.getStartDate(), modifyRentDTO.getEndDate());
        if (updatedRent == null) {
            // se il noleggio non viene trovato, errore
            return ResponseEntity.notFound().build();
        }
        // noleggio aggiornato messaggio ok
        return new ResponseEntity<>(updatedRent, HttpStatus.OK);
    }
    // Elimina un noleggio
    @DeleteMapping("/remove/{userId}/{rentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRent(@PathVariable Long id) {
        // elimina il noleggio by id
        rentService.deleteRent(id);
    }


}


