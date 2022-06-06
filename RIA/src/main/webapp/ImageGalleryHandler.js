/**
 * 
 */
 
 {
	//componenti della mia pagine,
	//ho 2 liste: albumMiei e albumNONMiei
	//ogni Album ha immaginiInAlbum 
	//poi ci sono i dettagli dell'immagine e i commenti
	let albumMiei, albumNonMiei, creaAlbum, inserisciImmagineInAlbum, pageOrchestrator = new PageOrchestrator();


	//controllo che lo username sia in sessione,
	//se non lo è rimando alla index.html
	window.addEventListener("load", () => {  											//controllo che lo username sia in sessione, se lo è avvio il pageOrchestrator altrimenti lo mando alla index.html
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // inizializza il componente
	      pageOrchestrator.refresh();
	    }
	  }, false);	


	function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;    //nel messagecontainer, cioè nello span con id=id_username, metto il nome dell'utente
	    }
	}
	
	
	function AlbumMiei(_alert, _containerAlbumMiei, _bodyAlbumMiei){ 
		this.alert = _alert
		this.containerAlbumMiei=_containerAlbumMiei;
		this.bodyAlbumMiei=_bodyAlbumMiei;
		
		//reset mi nasconde la tabella contenente tutti i miei album
		this.reset = function() { 
	      this.containerAlbumMiei.style.visibility = "hidden";
	    };
	    
	    this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetAlbumsMiei", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var albumsToShow = JSON.parse(req.responseText);
	              if (albumsToShow.length == 0) {
	                self.alert.textContent = "Non ci sono Album";
	                return;
	              }
	              self.update(albumsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };
	    
	    
	   this.update = function(arrayAlbums) { 
	      var elem, i, row, titlecell, idcell, datacell, anchor;
	      this.bodyAlbumMiei.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayAlbums.forEach(function(album) { // self visible here, not this
	        row = document.createElement("tr");
	        linkcell = document.createElement("td")
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode(album.titolo);
	        anchor.appendChild(linkText);
	        anchor.setAttribute('albumTitolo', album.titolo); // set a custom HTML attribute
	        
	        anchor.addEventListener("click", (e) => {
	          // dependency via module parameter
	          missionDetails.show(e.target.getAttribute("albumTitolo")); // the list must know the details container
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        datacell = document.createElement("td");
	        datacell.textContent = album.data_creazione;
	        row.appendChild(datacell);
	        self.bodyAlbumMiei.appendChild(row);
	      });
	      this.containerAlbumMiei.style.visibility = "visible";

	    };
	    
	    
	    this.autoclick = function(titoloAlbum) {
	      var e = new Event("click");
	      var selector = "a[titoloAlbum='" + titoloAlbum + "']";
	      var anchorToClick =
	        (titoloAlbum) ? document.querySelector(selector) : this.bodyalbumMiei.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }
	    
		
	}
	
	function AlbumNonMiei(_alert, _containerAlbumNonMiei, _bodyAlbumNonMiei){
		this.alert = _alert 
		this.containerAlbumNonMiei=_containerAlbumNonMiei;
		this.bodyAlbumNonMiei=_bodyAlbumNonMiei;
		
		//reset mi nasconde la tabella contenente tutti i miei album
		this.reset = function() {  
	      this.containerAlbumNonMiei.style.visibility = "hidden";
	    };
	    
	    this.show = function(next) {
	      var self = this; 
	      makeCall("GET", "GetAlbumsNonMiei", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var albumsToShow = JSON.parse(req.responseText);
	              if (albumsToShow.length == 0) {
	                self.alert.textContent = "Non ci sono Album";
	                return;
	              }
	              self.update(albumsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	    };
	    
	    
	   this.update = function(arrayAlbums) { 
	      var elem, i, row, titlecell, idcell, datacell, anchor;
	      this.bodyAlbumNonMiei.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayAlbums.forEach(function(album) { // self visible here, not this
	        row = document.createElement("tr");
	        
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode(album.titolo);
	        anchor.appendChild(linkText);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        
	        idcell = document.createElement("td");
	        idcell.textContent = album.user_id;
	        row.appendChild(idcell);
	        datacell = document.createElement("td");
	        datacell.textContent = album.data_creazione;
	        row.appendChild(datacell);
	        self.bodyAlbumNonMiei.appendChild(row);
	      });
	      this.containerAlbumNonMiei.style.visibility = "visible";

	    };
	    
	    
	    this.autoclick = function(titoloAlbum) {
	      var e = new Event("click");
	      var selector = "a[titoloAlbum='" + titoloAlbum + "']";
	      var anchorToClick =  // the first mission or the mission with id = missionId
	        (titoloAlbum) ? document.querySelector(selector) : this.bodyalbumMiei.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    };
	    
	    
		
	}
	
	function CreaAlbum(){
	    
	    this.registraEvento = function(orchestrator){
	    	document.getElementById("id_button_creaAlbum").addEventListener('click', (e) => {  //prende il bottone che ha id=loginbutton e quando viene cliccato fa l'evento e
    		var form = e.target.closest("form");   
    		if (form.checkValidity()) {
	
				makeCall("POST", 'CreateAlbum', e.target.closest("form"),				
		     	function(x) {
		          if (x.readyState == XMLHttpRequest.DONE) {                        //se il server mi ha risposto entro nell'if
		          
		          		//x.responseText prende il nome dell'utente che CheckLogin ha messo in: response.getWriter().println(usrn); 
		          		//oppure in response.getWriter().println("Incorrect credentials"); nel caso l'utente non esiste
		            var message = x.responseText;									
		            switch (x.status) {
						case 200:
	                  		orchestrator.refresh(); // id of the new mission passed
	                  		document.getElementById("ok_createAlbum").textContent = message;
	                  		document.getElementById("errore_createAlbum").textContent = "";
	                  		
	                  	break;
	                  	case 500:
	                  		document.getElementById("errore_createAlbum").textContent = message;
	                  		document.getElementById("ok_createAlbum").textContent = "";
	                  	case 400:
	                  		document.getElementById("errore_createAlbum").textContent = message;
	                  		document.getElementById("ok_createAlbum").textContent = "";
		            	case 403:
	                   		window.location.href = req.getResponseHeader("Location");
	                    	window.sessionStorage.removeItem('username');
	                    break;
		            }
		          }
		          }
		          );
		        } else {
    	 			form.reportValidity(); //ritorna il motivo per cui la checkValidity non è andata a buon fine
    			}  
	    	});	
		};
					
	}
	
	function InserisciImmagineInAlbum(_nomeAlbum, _nomeImmagine){  
		
		this.nomeAlbum = _nomeAlbum;
		this.nomeImmagine = _nomeImmagine;
		
		this.registraEventoadd = function(orchestrator){ 
			document.getElementById("id_button_insertIntoAlbum").addEventListener('click', (e) => { 
				var form = e.target.closest("form");  
				if (form.checkValidity()) {
	
					makeCall("POST", 'InsertIntoAlbum', e.target.closest("form"),				
		     		function(x) {
		          		if (x.readyState == XMLHttpRequest.DONE) {                        //se il server mi ha risposto entro nell'if
		          
		          		//x.responseText prende il nome dell'utente che CheckLogin ha messo in: response.getWriter().println(usrn); 
		          		//oppure in response.getWriter().println("Incorrect credentials"); nel caso l'utente non esiste
		            	var message = x.responseText;									
		            	switch (x.status) { 
							case 200:
	                  		orchestrator.refresh(); // id of the new mission passed
	                  		document.getElementById("ok_addImage").textContent = message;
	                  		document.getElementById("errore_addImage").textContent = "";
	                  		break;
	                  		case 500:
	                  		document.getElementById("errore_addImage").textContent = message;
	                  		document.getElementById("ok_addImage").textContent = "";
	                  		case 400:
	                  		document.getElementById("errore_addImage").textContent = message;
	                  		document.getElementById("ok_addImage").textContent = "";
		            		case 403:
	                   		window.location.href = req.getResponseHeader("Location");
	                    	window.sessionStorage.removeItem('username');
	                    	break;
		            	}
		         	 }
		          }
		          );
		        } else {
    	 			form.reportValidity(); //ritorna il motivo per cui la checkValidity non è andata a buon fine
    			}  
				});
	    	
		};
		
			
		this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetAlbumsMiei", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var albumsToShow = JSON.parse(req.responseText);
	              if (albumsToShow.length == 0) {
	                return;
	              }
	              self.update(albumsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
               }
	        }
	      );
	    };
	    
	    
	  this.update = function(arrayAlbums) { 
	      var text, option;
	      
	      // build updated list
	      var self = this;
	      arrayAlbums.forEach(function(album) { // self visible here, not this
	        option = document.createElement("option");
	        text = document.createTextNode(album.titolo);
	        option.appendChild(text);
	        self.nomeAlbum.appendChild(option);
	      });

	    };
	    
	    
	    this.showimm = function(next) {
	      var self = this;
	      makeCall("GET", "GetNewImages", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var imgsToShow = JSON.parse(req.responseText);
	              if (imgsToShow.length == 0) {
	                return;
	              }
	              self.updateimm(imgsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
               }
	        }
	      );
	    };
	    
	    this.updateimm = function(arrayImages) { 
	      var text, option;
	      var self = this;
	      // build updated list
	      
	      arrayImages.forEach(function(immagine) { // self visible here, not this
	        option = document.createElement("option");
	        text = document.createTextNode(immagine.titolo);
	        option.appendChild(text);
	        self.nomeImmagine.appendChild(option);
	      });

	    };
	    
	    this.reset = function(){
			var self = this;

			var select = self.nomeImmagine;
			var length = select.options.length; 
			for (i = length-1; i > -1; i--) { select.options[i] = null; }
		
		};
	    
	 
	}
	
	//--------------------------------------------------------------------------------------------------------------------
	function PageOrchestrator() {
		
		var alertContainer = document.getElementById("id_alert");
		
		//la funzione start inizializza le cose della mia Home Page
	    this.start = function() {
	      personalMessage = new PersonalMessage(sessionStorage.getItem('username'), document.getElementById("id_username"));    //PersonalMessage dichiarato su
	      personalMessage.show();
						
						
		albumMiei = new AlbumMiei(alertContainer, document.getElementById("id_albumMieiContainer"), document.getElementById("id_albumMieiBody"));				
		albumNonMiei = new AlbumNonMiei(alertContainer, document.getElementById("id_albumNonMieiContainer"), document.getElementById("id_albumNonMieiBody"));
							 
		creaAlbum = new CreaAlbum();					 
		creaAlbum.registraEvento(this);
		
		inserisciImmagineInAlbum = new InserisciImmagineInAlbum(document.getElementById("id_nomeAlbum"), document.getElementById("id_nomeImmagine"));
		inserisciImmagineInAlbum.registraEventoadd(this);
		
		//chiamerà con l'ancora il logour e toglierà lo username dalla sessione
	    document.querySelector("a[href='Logout']").addEventListener('click', () => {
	    	window.sessionStorage.removeItem('username');
	      })
	    };
	    
	    this.refresh = function(boh) {
			alertContainer.textContent = ""; 
			albumMiei.reset();
			albumMiei.show();
			albumNonMiei.reset();
			albumNonMiei.show();
			inserisciImmagineInAlbum.reset();
			inserisciImmagineInAlbum.show();
			inserisciImmagineInAlbum.showimm();
	    };
	    
	}


}