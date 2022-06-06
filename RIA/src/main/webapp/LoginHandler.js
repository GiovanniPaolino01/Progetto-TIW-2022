/**
 * Login & Registration Handler
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {  //prende il bottone che ha id=loginbutton e quando viene cliccato fa l'evento e
    var form = e.target.closest("form");   									 //cerca la form più vicina a quel bottone
    if (form.checkValidity()) {                                               //controlla validità della form
    
    	//Fa la chiamata della Servlet CheckLogin passandogli gli elementi della form, 
    	//la CheckLogin farà get dei parametri username e pwd della form in index.html
    	//E' una funzione di CallBack, fornisce al server tutti i parametri per elaborare la richiesta, ma non aspettano la risposta
    	//sono asincrone, infine passano come parametro una funzione, che verrà richiamata nel momento in cui il client si rende conto che la risposta del server è disponibile
    	//x è la richiesta che faccio e in x.responseText c'è la risposta del server 
  	makeCall("POST", 'CheckLogin', e.target.closest("form"),				
     	function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {                        //se il server mi ha risposto entro nell'if
          
          		//x.responseText prende il nome dell'utente che CheckLogin ha messo in: response.getWriter().println(usrn); 
          		//oppure in response.getWriter().println("Incorrect credentials"); nel caso l'utente non esiste
            var message = x.responseText;									
            switch (x.status) {
              case 200:
            	sessionStorage.setItem('username', message);				//setta il parametro in sessione
                window.location.href = "Home.html";        				 //mi reindirizza alla pagina Home.html
                break;
              case 400: // bad request
                document.getElementById("errormessage_login").textContent = message;  //se non va a buon fine, nella index.html c'è un p che ha id=errormessage a cui viene assegnato il messaggio
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage_login").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessage_login").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity(); //ritorna il motivo per cui la checkValidity non è andata a buon fine
    }
  });
})(); //() comporta l'invocazione immediata di questa funzione