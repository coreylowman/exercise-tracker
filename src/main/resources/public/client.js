var ascendingOrder = false;

window.onload = loadData;

function todaysDate() {
    var local = new Date();
    local.setMinutes(local.getMinutes() - local.getTimezoneOffset());
    return local.toJSON().slice(0,10);
}

function loadData() {
    document.getElementById('select_date').value = todaysDate();
    query("GET", "/entries", renderEntries, null);
    query("GET", "/exercises", loadExercises, null);
    query("GET", "/bodyParts", loadBodyParts, null);
}

const EXERCISES = {};
const BODYPARTS = {};

var loadedExercises = false;
var loadedBodyparts = false;

function loadExercises(jsonArray) {
    loadedExercises = true;
    var select = document.getElementById("select_exercise");
    for(var i = 0;i < jsonArray.length;i++) {
        var opt = document.createElement('option');
        opt.value = jsonArray[i].id;
        opt.text = jsonArray[i].name;
        select.appendChild(opt);
        EXERCISES[jsonArray[i].id] = jsonArray[i];
    }

    if(loadedBodyparts) {
        exerciseOnChange();
    }
}

function loadBodyParts(jsonArray) {
    loadedBodyparts = true;
    var select = document.getElementById("select_bodyPart");
    for(var i = 0;i < jsonArray.length;i++) {
        var opt = document.createElement('option');
        opt.value = jsonArray[i].id;
        opt.text = jsonArray[i].name;
        select.appendChild(opt);
        BODYPARTS[jsonArray[i].id] = jsonArray[i];
    }

    if(loadedExercises) {
        exerciseOnChange();
    }
}

const entryKeys = ["date", "exerciseName", "bodyPartName", "numReps", "repDuration_sec", "repWeight_lbs", "note"];

function renderEntries(jsonArray) {
    var entryTable = document.getElementById("entries");

    var dates = [];
    var exercises = {};

    for(var i = 0;i < jsonArray.length;i++) {
        var entry = jsonArray[i];
        var row = entryTable.insertRow(-1);
        for (var j = 0;j < entryKeys.length;j++) {
            var k = entryKeys[j];
            var cell = row.insertCell(-1);
            if (entry.hasOwnProperty(k)) {
                cell.innerHTML = entry[k];
            }
        }

        var actionsCell = row.insertCell(-1);
        actionsCell.innerHTML = "<button onclick=\"removeEntry(" + entry.id + ")\">delete</button>";

        var date = entry.date;
        if (dates.indexOf(date) == -1) {
            dates.push(date);
        }

        var exercise = entry.exerciseName;
        if (exercises.hasOwnProperty(exercise)) {
            exercises[exercise].push(entry);
        } else {
            exercises[exercise] = [entry];
        }
    }

    dates.sort(byDate);
    var exercisesHeader = document.getElementById("exercisesHeader");
    for (var i = 0;i < dates.length;i++) {
        var cell = exercisesHeader.insertCell(-1);
        cell.innerHTML = dates[i];
    }

    var exercisesTable = document.getElementById("exercises");
    for(var exerciseName in exercises) {
        var row = exercisesTable.insertRow(-1);

        var header = document.createElement("th");
        header.innerHTML = exerciseName;
        row.appendChild(header);

        exercises[exerciseName].sort(function (exer1, exer2) {
            return byDate(exer1.date, exer2.date);
        });

        var i = 0;
        var arr = exercises[exerciseName];
        for(var j = 0;j < dates.length;j++) {
            var cell = row.insertCell(-1);
            if (dates[j] === arr[i].date) {
                cell.setAttribute("bgcolor", "green");
                i++;
            }
        }
    }
}

function exerciseOnChange() {
    var exercise = document.getElementById('select_exercise').value;
    var bodypart = document.getElementById('exercise_bodypart');
    bodypart.value = BODYPARTS[EXERCISES[exercise].bodyPartId].name;
}

function removeEntry(id) {
    query("DELETE", "entries/" + id, updateEntries, null)
}

function updateEntries(resp) {

}

function byDate(str1, str2) {
    var date1 = new Date(str1);
    var date2 = new Date(str2);

    return date1.getTime() > date2.getTime();
}

function byInt(str1, str2) {
    var num1 = parseInt(str1);
    var num2 = parseInt(str2);

    return num1 > num2;
}

function byStringVal(str1, str2) {
    return str1.localeCompare(str2);
}

function sortByIndex(index, compareFunc) {
    ascendingOrder = !ascendingOrder;
    var tbl = document.getElementById("entries").tBodies[0];
    var store = [];
    for(var i=1, len=tbl.rows.length; i<len; i++){
        var row = tbl.rows[i];
        store.push([row.cells[index].textContent, row]);
    }
    store.sort(function(x, y) {
        if (ascendingOrder) {
            return compareFunc(x[0], y[0]);
        } else {
            return compareFunc(y[0], x[0]);
        }
    });
    for(var i=0, len=store.length; i<len; i++){
        tbl.appendChild(store[i][1]);
    }
    store = null;
}

function query(type, url, callback, body) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
        if (req.readyState == XMLHttpRequest.DONE && req.status == 200) {
            callback(JSON.parse(req.responseText));
        }
    }
    req.open(type, url, true);
    req.send(body);
}
