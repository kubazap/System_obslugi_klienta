(function ($) {
  "use strict";

  // Preloader
    const preloader = document.querySelector('#preloader');
    if (preloader) {
      window.addEventListener('load', () => {
        preloader.remove();
      });
    }

  // Timer
  $(function() {
    const timerEl   = document.getElementById('timer');
    if (timerEl) {
      let time       = parseInt(timerEl.dataset.remainingSeconds, 10) || 0;
      const submitBtn  = document.getElementById('submitBtn');
      const expiredMsg = document.getElementById('expired-message');

      updateTimerText(time);

      const interval = setInterval(() => {
        time--;
        if (time >= 0) {
          updateTimerText(time);
        }
        if (time < 0) {
          clearInterval(interval);
          submitBtn.disabled = true;
          document.querySelectorAll('.pin-input').forEach(i => i.disabled = true);
          expiredMsg.style.display = 'block';
          timerEl.style.display   = 'none';
        }
      }, 1000);

      function updateTimerText(sec) {
        const m = String(Math.floor(sec / 60)).padStart(2, '0');
        const s = String(sec % 60).padStart(2, '0');
        timerEl.textContent = `${m}:${s}`;
      }
    }
  });

  // TOTP
  $(function() {
    const inputs = Array.from(document.querySelectorAll('.pin-input'));
    if (inputs.length === 0) return;

    inputs.forEach((el, idx) => {
      el.addEventListener('keyup', (e) => {
        if (e.key === 'Backspace' && idx > 0 && !el.value) {
          inputs[idx - 1].focus();
        } else if (el.value && idx < inputs.length - 1) {
          inputs[idx + 1].focus();
        }
      });
      el.addEventListener('input', () => {
        el.value = el.value.replace(/[^0-9]/g, '');
      });
    });
    inputs[0].focus();

    window.collectCode = function(ev) {
      const code = inputs.map(i => i.value).join('');
      if (code.length !== 6) {
        ev.preventDefault();
        alert('Kod musi mieć 6 cyfr!');
        return false;
      }
      document.getElementById('totpFull').value = code;
    };
  });

  // Toggle the side navigation
  $("#sidebarToggle, #sidebarToggleTop").on("click", function () {
    $("body").toggleClass("sidebar-toggled");
    $(".sidebar").toggleClass("toggled");
    if ($(".sidebar").hasClass("toggled")) {
      $(".sidebar .collapse").collapse("hide");
    }
  });

  // Close any open menu accordions when window is resized below 768 px
  $(window).resize(function () {
    if ($(window).width() < 768) {
      $(".sidebar .collapse").collapse("hide");
    }

    // Toggle the side navigation when window is resized below 480 px
    if ($(window).width() < 480 && !$(".sidebar").hasClass("toggled")) {
      $("body").addClass("sidebar-toggled");
      $(".sidebar").addClass("toggled");
      $(".sidebar .collapse").collapse("hide");
    }
  });

  // Prevent the content wrapper from scrolling when the fixed side navigation is hovered over
  $("body.fixed-nav .sidebar").on("mousewheel DOMMouseScroll wheel", function (e) {
    if ($(window).width() > 768) {
      const e0 = e.originalEvent,
        delta = e0.wheelDelta || -e0.detail;
      this.scrollTop += (delta < 0 ? 1 : -1) * 30;
      e.preventDefault();
    }
  });

  // Scroll‑to‑top button appear
  $(document).on("scroll", function () {
    const scrollDistance = $(this).scrollTop();
    if (scrollDistance > 100) {
      $(".scroll-to-top").fadeIn();
    } else {
      $(".scroll-to-top").fadeOut();
    }
  });
})(jQuery);
