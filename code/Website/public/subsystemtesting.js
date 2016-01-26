function test_login(username, password ) {
   // results.total++;
    var name = getUserName();
    var password = getUserPassword();

    if (name !== username) {
      //results.bad++;
      console.log("Expected " + username +
        ", but was " + name);
    }
    else{console.log("Test Case for Login Passed!");}
  }
  
  test_login("mchiu002@fiu.edu", "password123");
  
  function test_account(expected){
    var telephone = getTelephone();
    if(telephone != expected){
      console.log("Expected " + expected + " , but was " + telephone);
    }
else{
  console.log("Test Case Passed!");
}
  }
