function convertRome() {
	console.log ("Convert Button was clicked");
	// getting string from input field
	let sRome = document.getElementById('romeField').value; 
	console.log ("Client send "+ sRome);
	// create an obj which automatically 
	// get values of inputs fields
	// it (obj) can be tranfered into POST-response
	let formData = new FormData (document.forms.romeToArabic);
	// create an obj which make HTTP-request
	let http_req = new XMLHttpRequest();
	// create a request-string
	http_req.open("POST","/rome_form/calculate");
	// sendind request (AND DATA FROM FORM!) to server
	http_req.send(formData);
	console.log ("Request was sent, waiting for response");
	http_req.onload = () => {
		let response = http_req.responseText;
		console.log ("Response: " + response);
		document.getElementById('outputField').value = response; 
	}
	console.log("Function just has finished");

}