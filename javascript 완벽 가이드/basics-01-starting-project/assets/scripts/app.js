const defaultResult = 0;
let currentResult = defaultResult;
let logEntries = [];

// 입력 필드에서 입력값 가져옴
function getUserNumberInput() {
  return parseInt(userInput.value);
}

// 계산 로그 생성과 작성
function createAdnWriteOutput(operator, resultBeforeCalc, calcNumber) {
  let calculationDescription = `${resultBeforeCalc} ${operator} ${calcNumber}`;
  outputResult(currentResult, calculationDescription); //vendor 파일에서 옴
}

function writeToLog(
  operationIdentifier, 
  prevResult, 
  opertationNumber, 
  newResult
) {
  const logEntry = {
    operation: operationIdentifier,
    prevResult: prevResult,
    number: opertationNumber,
    result: newResult
  };
  logEntries.push(logEntry);
  console.log(logEntries);
}

function calculateResult(calculationType) {
  const enteredNumber = getUserNumberInput();

  // 유효성 체크, 0이거나 사칙연산이 아닌경우 리턴
  if(
    calculationType !== 'ADD' && 
    calculationType !== 'SUBTRACT' && 
    calculationType !== 'MUTIPLY' && 
    calculationType !== 'DIVIDE' ||
    !enteredNumber
  ) {
    return;
  }
  
  const initialResult = currentResult;
  let mathOperator;

  if(calculationType === 'ADD') {
    currentResult += enteredNumber;
    mathOperator = '+';
  } else if(calculationType === 'SUBTRACT') {
    currentResult -= enteredNumber;
    mathOperator = '-';
  } else if(calculationType === 'MUTIPLY') {
    currentResult *= enteredNumber;
    mathOperator = '*';
  } else if(calculationType === 'DIVIDE') {
    currentResult /= enteredNumber;
    mathOperator = '/';
  }
  
  createAdnWriteOutput(mathOperator, initialResult, enteredNumber);
  writeToLog(calculationType, initialResult, enteredNumber, currentResult);
}

function add() {
  calculateResult('ADD');
}

function subtract() {
  calculateResult('SUBTRACT');
}

function multiply() {
  calculateResult('MUTIPLY');
}

function divide() {
  calculateResult('DIVIDE');
}

addBtn.addEventListener('click', add);
subtractBtn.addEventListener('click', subtract);
multiplyBtn.addEventListener('click', multiply);
divideBtn.addEventListener('click', divide);


