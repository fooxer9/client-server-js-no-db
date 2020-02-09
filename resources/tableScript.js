var rowNym = 0;

function onPageLoad() {
	var modal = document.querySelector("#modal");
	var closeButton = document.querySelector("#close_button");
	var add_button = document.getElementById("btn_add");
	var inp_name = document.getElementById("inp_name");
	var inp_genre = document.getElementById("inp_genre");
	var inp_link = document.getElementById("inp_link");
	add_button.addEventListener("click",function() {
		modal.classList.toggle("open");
		inp_name.value="";
		inp_link.value="";
		inp_genre.value="";
		window.rowNum = null;
	})

	

	close_button.addEventListener("click", function() {
		modal.classList.toggle("open");
		inp_name.value="";
		inp_link.value="";
		inp_genre.value="";


	});
	let http_req = new XMLHttpRequest();
	http_req.open("POST","/table_form/getTable");
	let formData = new FormData();
	formData.append('request','artists');
	http_req.send(formData);
	// server has to send list's size 
	http_req.onload = () => {
		let response = http_req.responseText;
		console.log(response);
		dataArr = response.split('\n');
		for (let i = 0; i < dataArr.length; i++) {
			let dataRow = dataArr[i].split(';');
			if (dataRow[0]) {
			makeRow(dataRow[0],dataRow[1],dataRow[2]);
		}
		}

	}
}



function makeRow(_name, _link, _genre) {
	// creating a row and add it to mainTable
	let name = _name;
	let link = _link;
	let genre = _genre;
	// finding mainTable in the doc
	let tbody = document.getElementById('mainTable').getElementsByTagName('tbody')[0];
	// creating a row
	let row = document.createElement('tr');
	//append row to table
	tbody.appendChild(row);
	//creating cells
	let name_td = document.createElement('td');
	name_td.innerHTML = name;

	let link_td = document.createElement('td');
	link_td.innerHTML = link;

	let genre_td = document.createElement('td');
	genre_td.innerHTML = genre;

	let del_td = document.createElement('td');
	del_td.classList.add('btn_delete');
	let del_btn = document.createElement('button');
	del_btn.addEventListener("click", function() {
		let row = this.closest('tr');
		window.rowNum = row.rowIndex;
		deleteNote(rowNum);

	})
	del_btn.innerHTML = 'DELETE';
	del_td.appendChild(del_btn);

	let chg_td =document.createElement('td');
	chg_td.classList.add('btn_change');
	chg_btn = document.createElement('button');
	chg_btn.addEventListener("click",function() {
		let row = this.closest('tr');
		window.rowNum = row.rowIndex;
		console.log(window.rowNum);
		let name = row.cells[0].innerText;
		let link = row.cells[1].innerText;
		let genre = row.cells[2].innerText;
		modal.classList.toggle("open");
		inp_name.value=name;
		inp_link.value=link;
		inp_genre.value=genre;
	
	})
	chg_btn.innerHTML = "CHANGE";
	chg_td.appendChild(chg_btn);

	// append cells into row

	row.appendChild(name_td);
	row.appendChild(link_td);
	row.appendChild(genre_td);
	row.appendChild(del_td);
	row.appendChild(chg_btn);



}

function deleteNote(row) {
	let http_req = new XMLHttpRequest();
	let form = new FormData();
	form.append('note', row);
	http_req.open("POST","/table_form/deleteNote");
	http_req.send(form);
	http_req.onload = () => {
		if( http_req.responseText == "delete ok") {
		var table = document.getElementById("mainTable");
		table.deleteRow(row);
	} else alert("Возникла ошибка сервера. Повторите операцию позже.")
	}

}

function addNote() {
	
       		let name_input = document.getElementById("inp_name").value;
       		let link_input = document.getElementById("inp_link").value;
       		let genre_input = document.getElementById("inp_genre").value;
       		sendNote(window.rowNum,name_input, link_input, genre_input);
       
}


function sendNote(rowId,_name, _link, _genre) {

	//creating request

	let http_req = new XMLHttpRequest();
	let form = new FormData();
	let note ="";

	if (rowId == null) {    // if row is null - we just create new note
		http_req.open("POST", "/table_form/addNote");
		note = _name+";"+_link+";"+_genre;
	}
	else {
		http_req.open("POST","/table_form/changeNote"); // if row is not null - we change note what exist
		note = rowId+";"+ _name+";"+_link+";"+_genre;
	}
	form.append("note",note);
	http_req.send(form);

	http_req.onload = () => {
		let response = http_req.responseText;
		console.log(response);
		if (response == 'add ok')
			makeRow(_name,_link,_genre); // create row with new note
		else if ((response == 'change ok') && (rowId != null))
			changeRow(rowId, _name, _link, _genre); //change row
		else alert("Произошла ошибка. Попробуйте снова");
		modal.classList.toggle("open"); // hide modal 

	}


}



function changeRow(rowId,_name,_link,_genre) {
	var table = document.getElementById("mainTable");
	var row = table.rows[rowId];
	row.cells[0].innerText = _name;
	row.cells[1].innerText = _link;
	row.cells[2].innerText = _genre;
}

