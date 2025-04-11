// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTableKlienci').DataTable({
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
  $('#dataTableWizyty').DataTable({
    autoWidth: false,
    columnDefs: [
          { targets: 0, width: '10%' },
          { targets: 1, width: '15%' },
          { targets: 2, width: '20%' },
          { targets: 3, width: '15%' },
          { targets: 4, width: '15%' },
          { targets: 5, width: '20%' },
          { targets: 6, width: '5%' }
      ],
    destroy: true,
    language: {
      url: 'vendor/datatables/pl.json'
    }
  });
  $('#dataTableDokumenty').DataTable({
      autoWidth: false,
      columnDefs: [
            { targets: 0, width: '30%' },
            { targets: 1, width: '20%' },
            { targets: 2, width: '20%' },
            { targets: 3, width: '23%' },
            { targets: 4, width: '7%' }
        ],
      destroy: true,
      language: {
        url: 'vendor/datatables/pl.json'
      }
    });
});
