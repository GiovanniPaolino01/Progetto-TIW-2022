/**
 * Registration management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("registrationbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    var password = document.getElementById("pw").value;
    var email = document.getElementById("username").value;
    var ripetipassword = document.getElementById("ripetipw").value;
    var regex_email_valida = /^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-]{2,})+.)+([a-zA-Z0-9]{2,})+$/;
    
  
    if (form.checkValidity() && password===ripetipassword && regex_email_valida.test(email)) {
      makeCall("POST", 'CheckRegistration', e.target.closest("form"),
        function(x) { //funzione di callback della makecall (makeCall definita in Utils.js)
          if (x.readyState == XMLHttpRequest.DONE) {
            var message = x.responseText;
            switch (x.status) {
              case 200: // OK
              	document.getElementById("errormessage_registration").textContent = "";
              	document.getElementById("okmessage_registration").textContent = message; /* Setto il messaggio di registrazione andata a buon fine */
                break;
              case 400: // bad request
                document.getElementById("errormessage_registration").textContent = message;
                break;
              case 500: // server error
            	document.getElementById("errormessage_registration").textContent = message;
                break;
            }
          }
        }
      );

    } else {
		if (password!==ripetipassword){
			//alert("Password e ripeti password devono essere uguali!");
			document.getElementById("errormessage_registration").textContent = "Password e Ripeti Password devono essere uguali!";
		}
		else if(!regex_email_valida.test(email)){
			document.getElementById("errormessage_registration").textContent = "Email sintatticamente scorretta!";
		}
		else{
			document.getElementById("errormessage_registration").textContent = "";
		}
		
		form.reportValidity();
    }
  });

})();