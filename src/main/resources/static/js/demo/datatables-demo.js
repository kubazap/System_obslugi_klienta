// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable({
    destroy: true,
    language: {
      url: 'vendor/datatables/pl.json'
    }
  });
});
