package com.lyra.poc.chatbot.model;

import java.util.Optional;

import com.github.messenger4j.user.UserProfile;

/**
 * Holds conversation infors for a specific facebook user
 * @author mregragui
 *
 */
public class ConversationContext {
    
    private String email;
    private String facebookId;
    private Integer amount;
	/**
	 * contains first name, last name, gender, url of profile pic, timezone and locale
	 */
    private UserProfile userProfile;

	public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFacebookId() {
        return facebookId;
    }
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
}
