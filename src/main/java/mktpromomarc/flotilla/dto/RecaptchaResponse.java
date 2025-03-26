package mktpromomarc.flotilla.dto;



public class RecaptchaResponse {

    Boolean success;
    String challenge_ts;
    String hostname;
    Double score;
    String action;

    public RecaptchaResponse() {
    }

    public RecaptchaResponse(Boolean success, String challenge_ts, String hostname, Double score, String action) {
        this.success = success;
        this.challenge_ts = challenge_ts;
        this.hostname = hostname;
        this.score = score;
        this.action = action;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getChallenge_ts() {
        return challenge_ts;
    }

    public void setChallenge_ts(String challenge_ts) {
        this.challenge_ts = challenge_ts;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

