import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom'; // Provides useful matchers
import FooterComponent from '../FooterComponent';

describe('FooterComponent', () => {
  it('renders footer with expected text', () => {
    render(<FooterComponent />);
    const footerText = screen.getByText(/WolfCafe © 2024/i);
    expect(footerText).toBeInTheDocument();
  });
});
