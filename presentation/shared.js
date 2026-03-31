(function () {
  function initializeDeck() {
    const slides = Array.from(document.querySelectorAll(".slide"));
    if (!slides.length) {
      return;
    }

    const counter = document.querySelector("[data-counter]");
    const progress = document.querySelector("[data-progress]");
    const title = document.querySelector("[data-slide-title]");
    const prevButton = document.querySelector("[data-prev]");
    const nextButton = document.querySelector("[data-next]");
    const dotsContainer = document.querySelector("[data-dots]");
    const jumpContainer = document.querySelector("[data-jumps]");

    let index = getIndexFromHash();

    slides.forEach(function (slide, slideIndex) {
      const label = slide.dataset.short || slide.dataset.title || "Slide " + (slideIndex + 1);

      if (dotsContainer) {
        const dot = document.createElement("button");
        dot.className = "dot";
        dot.type = "button";
        dot.setAttribute("aria-label", "Go to " + label);
        dot.addEventListener("click", function () {
          setIndex(slideIndex);
        });
        dotsContainer.appendChild(dot);
      }

      if (jumpContainer) {
        const link = document.createElement("button");
        link.className = "jump-link";
        link.type = "button";
        link.textContent = slideIndex + 1;
        link.title = label;
        link.addEventListener("click", function () {
          setIndex(slideIndex);
        });
        jumpContainer.appendChild(link);
      }
    });

    function getIndexFromHash() {
      const hash = window.location.hash.replace("#", "");
      const found = slides.findIndex(function (slide) {
        return slide.id === hash;
      });
      return found >= 0 ? found : 0;
    }

    function render() {
      slides.forEach(function (slide, slideIndex) {
        const active = slideIndex === index;
        slide.classList.toggle("active", active);
        slide.setAttribute("aria-hidden", String(!active));
      });

      const dots = Array.from(document.querySelectorAll(".dot"));
      const jumps = Array.from(document.querySelectorAll(".jump-link"));

      dots.forEach(function (dot, dotIndex) {
        dot.classList.toggle("active", dotIndex === index);
      });

      jumps.forEach(function (jump, jumpIndex) {
        jump.classList.toggle("active", jumpIndex === index);
      });

      if (counter) {
        counter.textContent = String(index + 1).padStart(2, "0") + " / " + String(slides.length).padStart(2, "0");
      }

      if (progress) {
        progress.textContent = slides[index].dataset.progress || "";
      }

      if (title) {
        title.textContent = slides[index].dataset.title || "";
      }

      if (prevButton) {
        prevButton.disabled = index === 0;
      }

      if (nextButton) {
        nextButton.disabled = index === slides.length - 1;
      }

      window.location.hash = slides[index].id;
    }

    function setIndex(nextIndex) {
      index = Math.max(0, Math.min(slides.length - 1, nextIndex));
      render();
    }

    if (prevButton) {
      prevButton.addEventListener("click", function () {
        setIndex(index - 1);
      });
    }

    if (nextButton) {
      nextButton.addEventListener("click", function () {
        setIndex(index + 1);
      });
    }

    window.addEventListener("keydown", function (event) {
      if (event.target && /input|textarea|select/i.test(event.target.tagName)) {
        return;
      }

      if (event.key === "ArrowRight" || event.key === "PageDown" || event.key === " ") {
        event.preventDefault();
        setIndex(index + 1);
      }

      if (event.key === "ArrowLeft" || event.key === "PageUp") {
        event.preventDefault();
        setIndex(index - 1);
      }

      if (event.key.toLowerCase() === "f" && document.documentElement.requestFullscreen) {
        document.documentElement.requestFullscreen();
      }
    });

    window.addEventListener("hashchange", function () {
      setIndex(getIndexFromHash());
    });

    render();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initializeDeck);
  } else {
    initializeDeck();
  }
})();
