package Communication;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReclamationDetails implements Serializable {
    protected String winnerMail;
    protected double winnerAmount;
    protected LocalDateTime winnerTime;
    protected String currentBidderMail;
    protected double currentBidAmount;
    protected String currentBidTime;

    public ReclamationDetails(String winnerMail, double winnerAmount, LocalDateTime winnerTime, String currentBidderMail, double currentBidAmount, String currentBidTime) {
        this.winnerMail = winnerMail;
        this.winnerAmount = winnerAmount;
        this.winnerTime = winnerTime;
        this.currentBidderMail = currentBidderMail;
        this.currentBidAmount = currentBidAmount;
        this.currentBidTime = currentBidTime;
    }

    public ReclamationDetails(String winnerMail, double winnerAmount, String winnerTime, String currentBidderMail, double currentBidAmount, String currentBidTime) {
        this(winnerMail, winnerAmount, LocalDateTime.parse(winnerTime), currentBidderMail, currentBidAmount, currentBidTime);
    }

    public String getWinnerMail() {
        return winnerMail;
    }

    public double getWinnerAmount() {
        return winnerAmount;
    }

    public LocalDateTime getWinnerTime() {
        return winnerTime;
    }

    public String getCurrentBidderMail() {
        return currentBidderMail;
    }

    public double getCurrentBidAmount() {
        return currentBidAmount;
    }

    public String getCurrentBidTime() {
        return currentBidTime;
    }


    @Override
    public String toString() {
        return  "\n Email du gagnant : " + winnerMail +
                ",\n Montant Gagnant : " + winnerAmount +
                ",\n Date de la vente final : " + winnerTime +
                ",\n Montant de votre enchère : " + currentBidAmount +
                ",\n Date de votre enchère : " + currentBidTime ;
    }
}
