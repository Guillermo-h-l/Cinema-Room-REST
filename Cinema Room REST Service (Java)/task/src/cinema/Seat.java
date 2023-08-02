package cinema;

import jakarta.servlet.http.PushBuilder;

public class Seat {
    private int row;
    private int column;
    private int price;
    public Seat(int row, int column) {
        this.column = column;
        this.row = row;
        this.price = row < 5 ? 10 : 8;
    }
    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;

        return (seat.row == this.row) && (seat.column == this.column);
    }
}