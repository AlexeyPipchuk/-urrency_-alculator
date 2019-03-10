package h.alexeypipchuk.calc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Answer {

    private Rates rates;

    public Rates getRates() {
        return rates;
    }

    public void setRates(Rates rates) {
        this.rates = rates;
    }

    public class Rates {

        public Double getrUB() {
            return rUB;
        }

        public void setrUB(Double rUB) {
            this.rUB = rUB;
        }

        @SerializedName("RUB")
        @Expose
        private Double rUB;

    }
}
