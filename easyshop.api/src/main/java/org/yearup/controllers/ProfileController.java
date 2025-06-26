package org.yearup.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RequestMapping("/profile")
@CrossOrigin
@RestController
public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;

    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public Profile getProfile(Principal principal) {
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            return profileDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving profile", e);
        }
    }

    @PutMapping
    public void updateProfile(@RequestBody Profile profile, Principal principal) {
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);

            profile.setUserId(user.getId());

            profileDao.update(profile);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating profile", e);
        }
    }
}


