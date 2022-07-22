/**
 * AJAX call management
 */
 
 /** VIENE CHIAMATA:
 	- nel LoginHandler, in cui la cback fa la checkValidity, se false fa reportValidity e se true fa la verifica dello status
 	  e setta l' errormessage_login dell'index nel caso di errori oppure, in caso di 200 OK inserisce in sessione lato client lo username
 	  e setta l'indirizzo della pagina corrente alla Home.html 
 	- nel RegistrationHandler
 	.....
*/

	function makeCall(method, url, formElement, cback, reset = true) {
	    var req = new XMLHttpRequest(); // visibile dalla closure
	    req.onreadystatechange = function() {
	      cback(req)
	    }; /*closure, è in grado di vedere la variabile req della funzione padre makeCall*/
	    req.open(method, url); /* Setto il method e l'indirizzo a cui la request dovrà poi essere mandata (nel caso di Login alla CheckLogin) */
	    if (formElement == null) { /*se nella form non ci sono dati, invio al server la richiesta vuota*/
	      req.send();
	    } else { /*altrimenti, se la form è stata compilata invio i dati all'url passato (nel caso del login la servlet CheckLogin)*/
	      req.send(new FormData(formElement));
	    }
	    if (formElement !== null && reset === true) { /*svuoto i campi della form*/
	      formElement.reset();
	    }
	  }
	  
	  
	  
