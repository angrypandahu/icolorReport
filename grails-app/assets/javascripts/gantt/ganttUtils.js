function toGanttJson(data) {
    var ganttTask = {};
    ganttTask['id'] = data['id'];
    ganttTask['startDate'] = data['start_date'].toJSON();
    ganttTask['text'] = data['text'];
    ganttTask['duration'] = data['duration'];
    ganttTask['type'] = data['type'];
    ganttTask['progress'] = data['progress'];
    ganttTask['parent'] = data['parent'];
    return ganttTask
}

function formatTime(date) {
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();

    var hour = date.getHours();
    var minute = date.getMinutes();
    var second = date.getSeconds();


    return [year, month, day].map(formatNumber).join('-') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

function formatNumber(n) {
    n = n.toString();
    return n[1] ? n : '0' + n
}

function joinUsers(users, allUsers) {
    if (!users) {
        return ""
    } else {
        var names = [];
        for (var i = 0; i < users.length; i++) {
            var user = users[i];
            names.push(getValueByKey(user, allUsers));
        }
        return names.join("/")
    }

}

function getValueByKey(id, arr) {
    var res = "";
    for (var i = 0; i < arr.length; i++) {
        var obj = arr[i];
        if (obj.key == id) {
            res = obj.label;
            break
        }
    }
    return res

}