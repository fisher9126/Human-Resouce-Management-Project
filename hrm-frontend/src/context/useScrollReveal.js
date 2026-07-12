import { useEffect } from 'react';

// Them class .reveal cho phan tu -> tu hien dan khi scroll toi.
// Goi useScrollReveal() 1 lan trong trang; observer chay lai khi doi deps.
export default function useScrollReveal(deps = []) {
  useEffect(() => {
    const els = document.querySelectorAll('.reveal:not(.reveal-visible)');
    if (!els.length) return;

    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting) {
            e.target.classList.add('reveal-visible');
            io.unobserve(e.target);
          }
        });
      },
      { threshold: 0.12 }
    );
    els.forEach((el) => io.observe(el));
    return () => io.disconnect();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);
}
