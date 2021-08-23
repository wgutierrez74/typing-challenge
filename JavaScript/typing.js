
const sampleTexts = [
	"Hello World",
    "Basketball is my favorite sport. I like it when they dribble up and down the court.",
    "Just a small town girl, living in a lonely world. She took the midnight train going anywhere.",
    "I have a problem that I can not explain. I have no reason why it should have been so plain. " +
    	"Have no questions but I sure have excuse. I lack the reason why I should be so confused.",
    "Everybody take it to the top. We're gonna stomp all night. In the neighborhood, don't it feel alright? " +
    	"We're gonna stomp all night. Wanna party till the morning light."
];

//Variables required for execution of Typing Challenge
let sampleText;
let typedText;
let timer;
let timerInterval;
let timeElapsed;
let blankSpace;
let cursorPosition;


const appLoad = () =>{
	document.getElementById("sample-pane").style.height=window.innerHeight * 0.75;
	setupBuild();
}

//Used to set up default settings for app
const setupBuild = () =>{
	
	//Stop timer and set variables to default values
	clearInterval(timerInterval);
	setSampleText();
	typedText="";
	cursorPosition=0;
	timer=false;
	blankSpace=true;
	
	//Change view elements to reflect starting values
	document.getElementById("time-elapsed").innerHTML="0:00";
	document.getElementById("words-typed").innerHTML="0";
	document.getElementById("words-per-minute").innerHTML="";
	document.getElementById("error-percentage").innerHTML="";
	
	//Hide certain view elements initially
	document.getElementById("wpm-title").style.visibility="hidden";
	document.getElementById("ep-title").style.visibility="hidden";
	
	//Add text area to bottom-pane and add an event listeners to respond to textArea events
	let bottomPane = document.getElementById("bottom-pane");
	bottomPane.innerHTML="";
	let textArea = document.createElement("TEXTAREA");
	textArea.className="input mt-1 mb-1";
	textArea.id="typed-text";
	
	textArea.addEventListener("input", {
	  handleEvent: updateText
	});

	textArea.addEventListener("click", {
		handleEvent: cursorPositionClick
	});

	//Add keydown listener when user types arrow keys
	textArea.addEventListener("keydown", {
		handleEvent: cursorPositionShift
	});
	
	
	bottomPane.appendChild(textArea);
}


const setSampleText = () =>{
	const randomNumber = Math.floor(Math.random() * sampleTexts.length);
	sampleText = sampleTexts[randomNumber];
	document.getElementById('sample-text').innerHTML =sampleText;
}

const updateText = (event) =>{
	if(!timer && event.data !== " "){
		startTimer();
		timer=true;
	}

	let textArea = document.getElementById("typed-text");

	//Prevents user from entering two blank spaces in a row
	if(blankSpace && event.data === " "){
		textArea.value = typedText;
		
		//Prevent default highlighting behavior when moving textArea cursor
		textArea.setSelectionRange(cursorPosition, cursorPosition);
		return;
	}

	typedText = textArea.value;
	document.getElementById("words-typed").innerHTML = typedText.length === 0 ? 0 : typedText.trim().split(" ").length;
	
	cursorPosition=textArea.selectionStart;
	blankSpace = determineBlankSpace();
	
	//Prevents user from typing a blank space into an empty textarea after challenge has started
	if(typedText === ""){
		blankSpace=true;
	}

	checkFinished();
}

const startTimer = () =>{
	timeElapsed = 0;
	timerInterval = setInterval(()=>{
		timeElapsed++;
		document.getElementById('time-elapsed').innerHTML = convertTime(timeElapsed);
	}, 1000)
}

//Converts number of seconds to string format mm:ss
const convertTime = (time) =>{
	let timeString = "";
	timeString += `${Math.floor(time/60)}:`;
	time -= 60*(Math.floor(time/60));
	if(time < 10){
		timeString += `0${time}`;
	}
	else{
		timeString += time;
	}
	return timeString;
}

//Determine if textArea cursorPosition is blank
const determineBlankSpace = () =>{
	if(cursorPosition === typedText.length && typedText.length > 0){
		return typedText[cursorPosition-1] === " ";
	}
	else if(cursorPosition === 0){
		return true;
	}
	else{
		return typedText[cursorPosition] === " " || typedText[cursorPosition-1] === " "; 
	}
}

const checkFinished = () =>{
	if(typedText.length === sampleText.length){
		clearInterval(timerInterval);
		
		//Calculate correct # of words per minute and display to screen
		const wpm = ((typedText.split(" ").length) / (timeElapsed/60)).toFixed(1);
		document.getElementById("words-per-minute").innerHTML = wpm;
		document.getElementById("wpm-title").style.visibility="visible";

		calculateErrors();
	}
}

const calculateErrors = () =>{
	//Remove children from bottom pane DIV
	let bottomPane = document.getElementById("bottom-pane");
	bottomPane.innerHTML = "";

	let results = document.createElement("DIV");
	results.id = "results";
	results.className = "input mt-1 mb-1"; 
	
	let errors = 0;
	for(let i=0;i<sampleText.length;i++){
		//Create SPAN and text element for each character the user typed
		let childNode = document.createElement("SPAN");
		childNode.appendChild(document.createTextNode(typedText[i]));
		if(typedText[i] !== sampleText[i]){
			errors++;
			childNode.className="error";
		}
		results.appendChild(childNode);
	}

	//Add results div to bottomPane
	bottomPane.appendChild(results);

	//Display error percentage
	document.getElementById("ep-title").style.visibility="visible";
	document.getElementById("error-percentage").innerHTML = `${((errors/sampleText.length)*100).toFixed(2)}%`;
}

//Determine proper cursorPosition when user types arrow keys
const cursorPositionShift = (event) =>{
	let currentPosition = document.getElementById("typed-text").selectionStart;
	if(event.key === "ArrowUp"){
		cursorPosition=0;
		blankSpace=true;
	}
	else if(event.key === "ArrowDown"){
		cursorPosition=typedText.length;
		blankSpace = determineBlankSpace();
	}
	else if(event.key === "ArrowLeft"){
		cursorPosition = currentPosition > 0 ? currentPosition-1 : 0;
		blankSpace = determineBlankSpace();
	}
	else if(event.key === "ArrowRight"){
		cursorPosition = currentPosition < typedText.length ? currentPosition+1 : typedText.length;
		blankSpace = determineBlankSpace();
	}
}

//Determine proper cursorPosition when user clicks on different parts of the textArea
const cursorPositionClick = (event) =>{
	let currentPosition = document.getElementById("typed-text").selectionStart;
	if(currentPosition === 0){
		cursorPosition = 0;
		blankSpace = true;
	}else{
		cursorPosition = currentPosition;
		blankSpace = determineBlankSpace();
	}
}