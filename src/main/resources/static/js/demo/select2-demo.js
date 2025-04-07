  $(document).ready(function() {
    // Inicjalizujemy Select2
    $('.js-select2').select2({
      placeholder: '-- Wybierz klienta --',
      allowClear: true,
      width: '100%' // aby zajęło pełną szerokość
    });
  });