package com.fontys.android.andr2.activities;

import com.fontys.android.andr2.helper.LoginEntryValidate;
import com.fontys.android.andr2.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class LoginActivityTest {


    @Test
    public void unitTestValidate() {
        //arrange
        //User user = mock(User.class);
        LoginEntryValidate entryValidate = new LoginEntryValidate();
        //act
        boolean results=entryValidate.validate("null@test.com","sssssss");
        //assert
        assertTrue("True........",results);
    }


    @Test
    public void unitTestValidate_wrong_entry() {
        //arrange
        //User user = mock(User.class);
        LoginEntryValidate entryValidate = new LoginEntryValidate();
        //act
        boolean results=entryValidate.validate("","");
        //assert
        assertFalse("error entry",results);
    }

    @Test
    public void unitTest_loginTest() {
        //arrange
        LoginActivity loginActivity = new LoginActivity();
        User user = Mockito.mock(User.class);
        user.getEmail();
        FirebaseUser firebaseUser=Mockito.mock(FirebaseUser.class);
        firebaseUser.getEmail();
        firebaseUser.getUid();
        FirebaseAuth firebaseAuth=Mockito.mock(FirebaseAuth.class);
        Task<AuthResult> results=firebaseAuth.signInWithEmailAndPassword( user.getEmail(),"ssss");
      //  firebaseAuth.signInWithEmailAndPassword(loginActivity.login( user.getEmail(),""))
//        /Assert.assert(results);
    }

}