// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable({
    autoWidth: false,
    columnDefs: [
          { targets: 0, width: '15%' },
          { targets: 1, width: '15%' },
          { targets: 2, width: '25%' },
          { targets: 3, width: '17%' },
          { targets: 4, width: '23%' },
          { targets: 5, width: '5%' }
      ],
    destroy: true,
    language: {
      url: 'vendor/datatables/pl.json'
    }
  });
});
