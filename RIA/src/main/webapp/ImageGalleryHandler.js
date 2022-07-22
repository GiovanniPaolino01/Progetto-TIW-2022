/**
 * ImageGalleryHandler
 */
 
 {
	//componenti della mia pagine,
	//ho 2 liste: albumMiei e albumNONMiei
	//ogni Album ha immaginiInAlbum 
	//poi ci sono i dettagli dell'immagine e i commenti
	let albumMiei, albumNonMiei, albumDetails, creaAlbum, inserisciImmagineInAlbum, creaCommento, commenti, pageOrchestrator = new PageOrchestrator();
	let ordinamentoAlbumMiei;
	
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
	
	function SetInfoAlbumMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;    //nel messagecontainer, cioè nello span con id=id_user_album, metto il nome dell'utente dell'album
	    }
	}
	
	
	function AlbumMiei(_alert, _containerAlbumMiei, _bodyAlbumMiei){ 
		this.alert = _alert;
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
	                self.alert.textContent = "Non ci sono album";
	                return;
	              }
	              self.update(albumsToShow); // self visible by closure
	              //if (next) next(); // show the default element of the list if present
	            
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
	    
	    this.showOrderByData = function(next) {
	      var self = this;
	      makeCall("GET", "GetAlbumsMieiOrderByData", null,
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
	              //if (next) next(); // show the default element of the list if present
	            
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
	      var elem, i, row, user, idcell, datacell, anchor;
	      this.bodyAlbumMiei.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayAlbums.forEach(function(album) { // self visible here, not this
	        row = document.createElement("tr");
	        row.draggable = true;
	        row.addEventListener("dragstart", (e)=>{dragStart(e);});
	        row.addEventListener("dragleave", (e)=>{dragLeave(e);});
	        row.addEventListener("dragover", (e)=>{dragOver(e);});
	        row.addEventListener("drop", (e)=>{drop(e);});
	        
	        linkcell = document.createElement("td")
	        
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode(album.titolo);
	        anchor.appendChild(linkText);
	        
	        anchor.setAttribute('albumTitolo', album.titolo); // set a custom HTML attribute
	        anchor.setAttribute('user_id', album.user_id);
	        anchor.addEventListener("click", (e) => {
	          // dependency via module parameter
	          albumDetails.reset();
	          albumDetails.showimm(e.target.getAttribute("albumTitolo"),e.target.getAttribute("user_id")); // the list must know the details container
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);

	        datacell = document.createElement("td");
	        datacell.textContent = album.data_creazione;
	        row.appendChild(datacell);
	        self.bodyAlbumMiei.appendChild(row);
	        
	      });
	      this.containerAlbumMiei.style.visibility = "visible";
		  self.autoclick(arrayAlbums)
	    };
	    
	    
	    this.autoclick = function(arrayAlbums) {
	      albumDetails.showimm(arrayAlbums[0].titolo, arrayAlbums[0].user_id);
	      //document.getElementById("id_nome_album").textContent = arrayAlbums[0].titolo;
	      //document.getElementById("id_user_album").textContent = arrayAlbums[0].user_id;
	    }
	    
		
	}
	
	function AlbumNonMiei(_alert, _containerAlbumNonMiei, _bodyAlbumNonMiei){ 
		this.alert = _alert;
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
	              //if (next) next(); // show the default element of the list if present
	            
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
	        anchor.setAttribute('albumTitolo', album.titolo); // set a custom HTML attribute
	        anchor.setAttribute('user_id', album.user_id);
	        anchor.addEventListener("click", (e) => { 
	          // dependency via module parameter
	          albumDetails.reset();
	          albumDetails.showimm(e.target.getAttribute("albumTitolo"),e.target.getAttribute("user_id")); // the list must know the details container
	        }, false);
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
							
	                  		orchestrator.refresh();
	                  		document.getElementById("ok_createAlbum").textContent = message;
	                  		document.getElementById("errore_createAlbum").textContent = "";
	                  		break;
	                  	case 500:
	                  		document.getElementById("errore_createAlbum").textContent = message;
	                  		document.getElementById("ok_createAlbum").textContent = "";
	                  		break;
	                  	case 400:
	                  		document.getElementById("errore_createAlbum").textContent = message;
	                  		document.getElementById("ok_createAlbum").textContent = "";
	                  		break;
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
	
	function InserisciImmagineInAlbum(_alert, _nomeAlbum, _nomeImmagine){  
		
		this.alert = _alert;
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
	                  		orchestrator.refresh(); 
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
					self.alert.textContent = "Non ci sono Album";
	                return;
	              }
	              self.update(albumsToShow); // self visible by closure
	              //if (next) next(); // show the default element of the list if present
	            
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else{
					 self.alert.textContent = message;
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
	              	self.updateimm(imgsToShow);
	          } else if (req.status == 403) {
                  	window.location.href = req.getResponseHeader("Location");
                  	window.sessionStorage.removeItem('username');
              } else{
					self.alert.textContent = message;
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

			//resetta la lista di immagini nuove, per poi chiamare la show e la show imm e riaggiornare l'elenco
			var select = self.nomeImmagine;
			var length = select.options.length; 
			for (i = length-1; i > -1; i--) { select.options[i] = null; }
			
			var select2 = self.nomeAlbum;
			var length2 = select2.options.length;
			for (i = length2-1; i > -1; i--) { select2.options[i] = null; }
		
		};
	    
	 
	}
	
	function AlbumDetails(_alert,_container,_miniatureImmagini){ 
		this.alert = _alert;
		this.container = _container;
		this.miniatureImmagini = _miniatureImmagini;
		let imgsToShow;
		let range;
		

		this.reset = function(){
			range = 0;
			this.container.style.visibility = "hidden";
			document.getElementById("id_button_next").style.visibility = "hidden";
			document.getElementById("id_button_previous").style.visibility = "hidden";
			
		};
		
		this.registraEvento = function(){
			document.getElementById("id_button_next").addEventListener('click', (e) => {
				
				range += 1;
				this.container.style.visibility = "hidden";
				this.updateimmagini(imgsToShow);

			});
			document.getElementById("id_button_previous").addEventListener('click', (e) => {
				
				range -= 1;
				this.container.style.visibility = "hidden";
				this.updateimmagini(imgsToShow);
			});
	     	
		};
		
		this.showimm = function(titoloAlbum, user_id){ 
			
			document.getElementById("id_nome_album").textContent = titoloAlbum;
	      	document.getElementById("id_user_album").textContent = user_id;
						
			var self = this;
	      makeCall("GET", "GetImage?titoloAlbum=" + titoloAlbum + "&user_id=" + user_id, null,
	        function(req) { 
	          if (req.readyState == 4) {
				 var message = req.responseText;
	            if (req.status == 200) {
	              imgsToShow = JSON.parse(req.responseText);
	              self.updateimmagini(imgsToShow); // self visible by closure
	              
	              //if (next) next(); // show the default element of the list if present
	            
	          	} else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                } else if(req.status == 500){
					self.alert.textContent = message;
				}
               }
	        });
		};

		
		this.updateimmagini = function(arrayImmagini) { 
	      	let row, image, idcell, titolo, anchor;
	      	this.miniatureImmagini.innerHTML = ""; // empty the table body
	      	
	     	
	     	if(arrayImmagini.length > (range+1)*5){
				document.getElementById("id_button_next").style.visibility = "visible";
			}
			else{
				document.getElementById("id_button_next").style.visibility = "hidden";
			}
			
			if(range>0){
				document.getElementById("id_button_previous").style.visibility = "visible";
			}else{
				document.getElementById("id_button_previous").style.visibility = "hidden";
			}
	     		     	
	     	
	     	if(arrayImmagini.length !== 0){
				row = document.createElement("tr");
				var dim_min = (range*5);
				var dim_max = ((range+1)*5);
				var i;
				
				/**console.clear;
				console.log("----------------------------------")
				console.log("min"+dim_min);
				console.log("max"+dim_max);
				console.log("dimensione");*/
				
				for(i=dim_min; i<dim_max && i<arrayImmagini.length; i++){
		    	
		        
		        idcell = document.createElement("td");
		        
		        /**image = document.createElement("img");
		        image.src = "data:image/png;base64,"+arrayImmagini[i].image;
		        image.style.width = "200px";
	        	idcell.appendChild(image);*/
				
				anchor = document.createElement("a");
	        	idcell.appendChild(anchor);
	        	
	        	
	        	image = document.createElement("img");
		        image.src = "data:image/png;base64,"+arrayImmagini[i].image;
		        image.style.width = "200px";
	        	anchor.appendChild(image);	
	        	
	        	//da fare
	        	anchor.setAttribute('immagineTitolo', arrayImmagini[i].titolo);
	        	anchor.setAttribute("immagineData", arrayImmagini[i].data);
	        	anchor.setAttribute("immagineTesto", arrayImmagini[i].testo);
	        	anchor.setAttribute("immagineTitoloAlbum", arrayImmagini[i].titolo_album);
	        	anchor.setAttribute("immagineUserAlbum", arrayImmagini[i].user_album);
	        	anchor.setAttribute("immagineFoto", arrayImmagini[i].image);

	        	//anchor.setAttribute("immaginePercorso", arrayImmagini[i].percorso);

	        	//anchor.setAttribute('user_id', album.user_id);
	        	anchor.className = "id_modale";		
				anchor.addEventListener("mouseover", (e) => {
					
						sessionStorage.setItem('titoloImmagine', e.target.closest("a").getAttribute("immagineTitolo"));
						sessionStorage.setItem('userImmagine', e.target.closest("a").getAttribute("immagineUserAlbum"));
						
					
	          			var modal = document.getElementById('modal');
	          			var container_immagine = document.getElementById("containerImmagine");
	          			var modal_content = document.getElementById("modal_content");
	          			var body = document.getElementById("id_body_modale");
	          			body.innerHTML = "";
	          			container_immagine.innerHTML = "";
	          			let titolo, data, testo, /*percorso,*/ titolo_album, user_album, row, foto;
	          			row = document.createElement("tr");
	          			
	          			
						titolo = document.createElement("td");
						titolo.textContent = "Titolo: "+e.target.closest("a").getAttribute("immagineTitolo");
						modal_content.style.visibility = "hidden";
	          			row.appendChild(titolo);
	          			body.appendChild(row);
	          			
	          			row = document.createElement("tr");
	          			data = document.createElement("td");
	          			data.textContent = "Data: "+e.target.closest("a").getAttribute("immagineData");
	          			row.appendChild(data);
	          			body.appendChild(row);
	          			
	          			row = document.createElement("tr");
	          			testo= document.createElement("td");
	          			testo.textContent = "Descrizione: "+e.target.closest("a").getAttribute("immagineTesto");
	          			row.appendChild(testo); 
	          			body.appendChild(row);
	          			
	          			row = document.createElement("tr");
	          			titolo_album= document.createElement("td");
	          			titolo_album.textContent = "Album: "+e.target.closest("a").getAttribute("immagineTitoloAlbum");
	          			row.appendChild(titolo_album); 
	          			body.appendChild(row);
	          			
	          			row = document.createElement("tr");
	          			user_album = document.createElement("td");
	          			user_album.textContent = "Proprietario: "+e.target.closest("a").getAttribute("immagineUserAlbum");
	          			row.appendChild(user_album); 
	          			body.appendChild(row);
	          			
	          			/*row = document.createElement("tr");
	          			percorso = document.createElement("td");
	          			percorso.textContent = "Percorso: "+e.target.closest("a").getAttribute("immaginePercorso");
	          			row.appendChild(percorso); 
	          			body.appendChild(row);*/
	          			
	          			foto = document.createElement("img");
	          			foto.src = "data:image/png;base64,"+e.target.closest("a").getAttribute("immagineFoto");
	          			foto.style.maxWidth = "800px";
	          			foto.style.maxHeight = "500px";
	          			container_immagine.appendChild(foto);
	          			
	          			commenti.reset();
	          			commenti.show();
	          			
	          			modal.style.display = "block";
	          			
	          			
	          			
	          			
	        	}, false);
	        	var modal_close = document.getElementsByClassName("modal_close")[0];
	        	modal_close.onclick = function() { modal.style.display = "none";
	        									   document.getElementById("ok_creaCommento").textContent = ""; }
	        	anchor.href = "#";
				
	        	titolo = document.createElement("p");
	        	titolo.textContent = arrayImmagini[i].titolo;
	        	idcell.appendChild(titolo);

		        row.appendChild(idcell);
		        
		        } 
		
		      this.miniatureImmagini.appendChild(row);
	      	  this.container.style.visibility = "visible";
	      }
	      
	    };
		
	}
	
	
	
	function CreaCommento(){
	    
	    this.registraEvento = function(){
	    	document.getElementById("id_button_creaCommento").addEventListener('click', (e) => {  //prende il bottone che ha id=loginbutton e quando viene cliccato fa l'evento e
    		var form = e.target.closest("form");   
    		if (form.checkValidity()) {
	
				makeCall("POST", "AddComment?titolo_immagine=" + window.sessionStorage.getItem("titoloImmagine") + "&user_immagine=" + window.sessionStorage.getItem("userImmagine") + "&testo_commento=" + document.getElementById("testo_commento").value, e.target.closest("form"),				
		     	function(x) {
		          if (x.readyState == XMLHttpRequest.DONE) {                        //se il server mi ha risposto entro nell'if
		          
		          		//x.responseText prende il nome dell'utente che CheckLogin ha messo in: response.getWriter().println(usrn); 
		          		//oppure in response.getWriter().println("Incorrect credentials"); nel caso l'utente non esiste
		            var message = x.responseText;									
		            switch (x.status) {
						case 200:
	                  		document.getElementById("ok_creaCommento").textContent = message;
	                  		document.getElementById("errore_creaCommento").textContent = "";
	                  		commenti.reset();
	                  		commenti.show();
	                  		break;
	                  	case 500:
	                  		document.getElementById("errore_creaCommento").textContent = message;
	                  		document.getElementById("ok_creaCommento").textContent = "";
	                  		break;
	                  	case 400:
	                  		document.getElementById("errore_creaCommento").textContent = message;
	                  		document.getElementById("ok_creaCommento").textContent = "";
	                  		break;
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
	
	

	
	function Commenti(_alert, _containerCommenti, _bodyCommenti){ 
		this.alert = _alert;
		this.containerCommenti=_containerCommenti;
		this.bodyCommenti=_bodyCommenti;
		
		//reset mi nasconde la tabella contenente tutti i commenti
		this.reset = function() { 
	      this.containerCommenti.style.visibility = "hidden";
	    };
	    
	    this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetComment?titoloImmagine="+ window.sessionStorage.getItem("titoloImmagine") + "&userImmagine="+ window.sessionStorage.getItem("userImmagine") , null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var commentsToShow = JSON.parse(req.responseText);
	              if (commentsToShow.length == 0) {
	                //self.alert.textContent = "Non ci sono commenti";
	                return;
	              }
	              self.update(commentsToShow); // self visible by closure
	              //if (next) next(); // show the default element of the list if present
	            
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
	    
	    
	   this.update = function(arrayComments) {  
	      var row, datacell, anchor;
	      this.bodyCommenti.innerHTML = ""; // empty the table body
	      // build updated list
	      var self = this;
	      arrayComments.forEach(function(commento) { // self visible here, not this
	        row = document.createElement("tr");

	        user = document.createElement("td");
	        user.textContent = commento.user_commento;
	        row.appendChild(user);
	        
	        
	        testo = document.createElement("td");
	        testo.textContent = commento.testo;
	        row.appendChild(testo);
	        self.bodyCommenti.appendChild(row);
	        
	      });
	      this.containerCommenti.style.visibility = "visible";
	    };
	 }

	
	function OdinamentoAlbumMiei(){
		
		this.registraEvento = function(){
			document.getElementById("id_salvaOrdinamento").addEventListener('click', (e)=>{
				this.salvaOrdinamento();
			})
		};
		
		this.registraEventoBottoneReset = function(){
			document.getElementById("id_resettaOrdinamento").addEventListener('click', (e)=>{
				albumMiei.reset();
				albumMiei.showOrderByData();
			})
		}
		
		
		this.salvaOrdinamento = function(){
				var lista = [];
				for(let i=0; i<document.getElementById("id_albumMieiBody").childElementCount ; i++){ 
					//console.log("children 0: "+document.getElementById("id_albumMieiBody").children[i].children[0].textContent);
					 lista[i] = document.getElementById("id_albumMieiBody").children[i].children[0].textContent;
					//console.log(lista[i]);
				}
				
				makeCall("POST", "SaveOrder?lista="+lista, null,
			        function(x) {
			          if (x.readyState == XMLHttpRequest.DONE) {                        //se il server mi ha risposto entro nell'if
				          
				          		//x.responseText prende il nome dell'utente che CheckLogin ha messo in: response.getWriter().println(usrn); 
				          		//oppure in response.getWriter().println("Incorrect credentials"); nel caso l'utente non esiste
				            var message = x.responseText;									
				            switch (x.status) {
								case 200:
			                  		document.getElementById("ok_salvaOrdinamento").textContent = message;
			                  		document.getElementById("errore_salvaOrdinamento").textContent = "";
			                  		break;
			                  	case 500:	
			                  		document.getElementById("errore_salvaOrdinamento").textContent = message;
			                  		document.getElementById("ok_salvaOrdinamento").textContent = "";
			                  		break;
				            }
				          }
			      });
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
							 
			albumDetails = new AlbumDetails(alertContainer, document.getElementById("id_container_miniatureImmagini"),document.getElementById("id_body_miniatureImmagini"));
			//poi da mettere albumDetails.registraEvento(this) per quando cliccare far uscire commenti ecc
	     	albumDetails.registraEvento();
	     	
	     	ordinamentoAlbumMiei = new OdinamentoAlbumMiei();	
			ordinamentoAlbumMiei.registraEvento();
			ordinamentoAlbumMiei.registraEventoBottoneReset();
	     					 			 
			creaAlbum = new CreaAlbum();					 
			creaAlbum.registraEvento(this);
						
			commenti = new Commenti(alertContainer, document.getElementById("id_commentiContainer"), document.getElementById("id_commentiBody"));

			creaCommento = new CreaCommento();					 
			creaCommento.registraEvento();
		
			inserisciImmagineInAlbum = new InserisciImmagineInAlbum(alertContainer, document.getElementById("id_nomeAlbum"), document.getElementById("id_nomeImmagine"));
			inserisciImmagineInAlbum.registraEventoadd(this);
		
			
		
			//chiamerà con l'ancora il logour e toglierà lo username dalla sessione
	    	document.querySelector("a[href='Logout']").addEventListener('click', () => {
	    		window.sessionStorage.removeItem('username');
	      	})
	    };
	    
	    this.refresh = function() {
			alertContainer.textContent = ""; 
			albumMiei.reset();
			albumDetails.reset();
			albumMiei.show();
			albumNonMiei.reset();
			albumNonMiei.show();
			inserisciImmagineInAlbum.reset();
			inserisciImmagineInAlbum.show();
			inserisciImmagineInAlbum.showimm();
	    };
	    
	}


}