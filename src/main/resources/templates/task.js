$(document).ready( function () {
    $('#myTable').DataTable();
} );

for (var i=0;i<links.length;i++){
    if (links[i].innerText.search('华为')){
        console.log(links[i].innerText)
    }
}