// Variable to save the reference of the dragged element
let startElement;


/* 
    This fuction puts all row to "notselected" class, 
    then we use CSS to put "notselected" in black and "selected" in red
*/
function unselectRows(rowsArray) {
    for (var i = 0; i < rowsArray.length; i++) {
        rowsArray[i].className = "notselected";
    }
}


/* 
    The dragstart event is fired when the user starts 
    dragging an element (if it is draggable=True)
    https://developer.mozilla.org/en-US/docs/Web/API/Document/dragstart_event
*/
function dragStart(event) {
    /* we need to save in a variable the row that provoked the event
     to then move it to the new position */
    startElement = event.target.closest("tr");
}

/*
    The dragover event is fired when an element 
    is being dragged over a valid drop target.
    https://developer.mozilla.org/es/docs/Web/API/Document/dragover_event
*/
function dragOver(event) {
    // We need to use prevent default, otherwise the drop event is not called
    event.preventDefault(); 

    // We need to select the row that triggered this event to marked as "selected" so it's clear for the user
    var dest = event.target.closest("tr");

    // Mark  the current element as "selected", then with CSS we will put it in red
    dest.className = "selected";
}

/*
    The dragleave event is fired when a dragged 
    element leaves a valid drop target.
    https://developer.mozilla.org/en-US/docs/Web/API/Document/dragleave_event
*/
function dragLeave(event) {
    // We need to select the row that triggered this event to marked as "notselected" so it's clear for the user 
    var dest = event.target.closest("tr");

    // Mark  the current element as "notselected", then with CSS we will put it in black
    dest.className = "notselected";
}

/*
    The drop event is fired when an element or text selection is dropped on a valid drop target.
    https://developer.mozilla.org/en-US/docs/Web/API/Document/drop_event
*/
function drop(event) {
    
    // Obtain the row on which we're dropping the dragged element
    var dest = event.target.closest("tr");

    // Obtain the index of the row in the table to use it as reference 
    // for changing the dragged element possition
    var table = dest.closest('table'); 
    var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
    var indexDest = rowsArray.indexOf(dest);

    // Move the dragged element to the new position
    if (rowsArray.indexOf(startElement) < indexDest)
        // If we're moving down, then we insert the element after our reference (indexDest)
        startElement.parentElement.insertBefore(startElement, rowsArray[indexDest + 1]);
    else
        // If we're moving up, then we insert the element before our reference (indexDest)
        startElement.parentElement.insertBefore(startElement, rowsArray[indexDest]);

    // Mark all rows in "not selected" class to reset previous dragOver
    unselectRows(rowsArray);
}
