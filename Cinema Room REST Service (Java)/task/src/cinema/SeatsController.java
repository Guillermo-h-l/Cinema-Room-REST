package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static cinema.Cinema.getAllSeats;

@RestController
public class SeatsController {
    private Cinema cinema;
    public SeatsController() {
        this.cinema = getAllSeats(9, 9);
    }
    @GetMapping("/seats")
    public Cinema getSeats() {
        return cinema;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> postPurchase(@RequestBody Seat seat) {
        if(seat.getRow() > cinema.getTotal_rows()       || seat.getRow() < 1    ||
        seat.getColumn() > cinema.getTotal_columns()    || seat.getColumn() < 1
        ) {
            return new ResponseEntity<>(
                    Map.of( "error",
                            "The number of a row or a column is out of bounds!"),
                    HttpStatus.BAD_REQUEST);

        }
        List<Seat> available_seats = cinema.getAvailable_seats();
        for (int i = 0; i < available_seats.size(); i++) {
            Seat res = available_seats.get(i);
            if (res.equals(seat)) {
                available_seats.remove(res);
                UnavailableSeat unavailableSeat = new UnavailableSeat(UUID.randomUUID(), res);
                cinema.getUnavailableSeats().add(unavailableSeat);
                return new ResponseEntity<>(unavailableSeat, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(
                Map.of("error", "The ticket has been already purchased!"),
                HttpStatus.BAD_REQUEST
        );
    }

    @PostMapping("/return")
    public ResponseEntity<?> postReturnSeat(@RequestBody Token token) {
        List<UnavailableSeat> unavailableSeats = cinema.getUnavailableSeats();
        for (UnavailableSeat unavailableSeat : unavailableSeats) {
            if (unavailableSeat.getToken().equals(token.getToken())) {
                unavailableSeats.remove(unavailableSeat);
                cinema.getAvailable_seats().add(unavailableSeat.getTicket());
                return new ResponseEntity<>(Map.of("returned_ticket", unavailableSeat.getTicket()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(Map.of("error", "Wrong token!"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> postStats(@RequestParam(required = false) String password) {
        if(password == null || !password.equals("super_secret")) {
            return new ResponseEntity<>(Map.of("error","The password is wrong!"), HttpStatus.valueOf(401));
        }
        Map<String, Integer> stats = new HashMap<String, Integer>();
        int currentIncome = 0;
        for (UnavailableSeat seat: cinema.getUnavailableSeats()) {
            currentIncome += seat.getTicket().getPrice();
        }
        stats.put("current_income", currentIncome);
        stats.put("number_of_available_seats", cinema.getAvailable_seats().size());
        stats.put("number_of_purchased_tickets", cinema.getUnavailableSeats().size());
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}

class Token {
    UUID token;

    public Token() { }
    public Token(UUID token) {
        this.token = token;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }
}
