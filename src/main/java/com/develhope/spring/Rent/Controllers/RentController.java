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

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            if (user.getUserType() == UserTypes.ADMIN) {
                Rent rent = rentService.createRent(rentDTO);
                if (rent == null) {
                    // se il noleggio e' null, errore
                    return ResponseEntity.badRequest().build();
                }
                return new ResponseEntity<>(rent, HttpStatus.CREATED);
            } else if (user.getUserType() == UserTypes.SELLER) {
                Rent rent = rentService.createRent(rentDTO);
                if (rent == null) {
                    // se il noleggio e' null, errore
                    return ResponseEntity.badRequest().build();
                }
                return new ResponseEntity<>(rent, HttpStatus.CREATED);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    // ottieni la lista dei noleggi
    @GetMapping("/list{userId}")
    public ResponseEntity<List<Rent>> rentList(@PathVariable Long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            if (user.getUserType() == UserTypes.BUYER) {
                List<Rent> rentals = user.getOrders();
                // Restituisci la lista dei noleggi messaggio ok
                return new ResponseEntity<>(rentals, HttpStatus.OK);
            }
        }

        return ResponseEntity.badRequest().build();
    }

    // ottieni un noleggio da id
    @GetMapping("/search/{userId}/{rentId}")
    public ResponseEntity<Rent> getRentById(@PathVariable Long userId, @PathVariable Long rentId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            if (user.getUserType() == UserTypes.BUYER) {
                Rent rent = rentService.getRentById(rentId);
                if (rent == null) {
                    // se il noleggio e' null, errore
                    return ResponseEntity.notFound().build();
                }
                // restituisci il noleggio trovato con messaggio ok
                return new ResponseEntity<>(rent, HttpStatus.OK);
            }
        }

        return ResponseEntity.badRequest().build();
    }

    // modifica le date di un noleggio
    @PutMapping("/update/{userId}/{rentId}")
    public ResponseEntity<Rent> updateRentDates(@PathVariable Long userId, @PathVariable Long rentId, @RequestBody ModifyRentDTO modifyRentDTO) {

        User user = userRepository.findbyId(userId).orElse(null);

        if (user != null) {
            if (user.getUserType() == UserTypes.ADMIN || user.getUserType() == UserTypes.SELLER) {
                Rent updatedRent = rentService.updateRentDates(rentId, modifyRentDTO.getStartDate(), modifyRentDTO.getEndDate());
                if (updatedRent == null) {
                    // se il noleggio non viene trovato, errore
                    return ResponseEntity.notFound().build();
                }
                // noleggio aggiornato messaggio ok
                return new ResponseEntity<>(updatedRent, HttpStatus.OK);
            }
        }

        return ResponseEntity.badRequest().build();
    }
    // Elimina un noleggio
    @DeleteMapping("/remove/{userId}/{rentId}")
    public ResponseEntity<Void> deleteRent(@PathVariable Long userId, @PathVariable Long rentId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            if (user.getUserType() == UserTypes.ADMIN || user.getUserType() == UserTypes.SELLER) {
                rentService.deleteRent(rentId);
                return ResponseEntity.noContent().build();
            } else if (user.getUserType() == UserTypes.BUYER) {
                // Rimuovi il noleggio dal cliente
                Rent rent = rentService.getRentById(rentId);
                if (rent != null && rent.getUser().getId().equals(userId)) {
                    rentService.deleteRent(rentId);
                    return ResponseEntity.noContent().build();
                }
            }
        }

        return ResponseEntity.badRequest().build();
    }

}


