import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import com.google.gson.Gson;


/**

*A simple data class to map the JSON response from the EXchangeRate API.
*/
class ExchangeRates {
    String base;
    Map<String, Double> conversion_rates;
}


/**
 *A currency converter GUI application using Swing and the ExchangeRate API.
 
 *Allows users to select currencies,input the amount and get the converted value.
 *
 
 * @author Tshepo Manamela
 */

public class CurrencyConverter extends JFrame implements ActionListener {
    JComboBox<String> to, from;
    JTextField amount;
    JButton swap, submit;
    JLabel resultLabel;
    
    
    /**
     *Constructor for the CurrencyConverter class.
     
     *Sets up the user interface.
     
     */

    public CurrencyConverter() {
        setupUI();
    }


    /**
     *Sets up the GUI components and layout for the application.
     
     */
    public void setupUI() {
        Color lightBlue = new Color(173, 216, 230); // Light blue (RGB)
        
        setTitle("Currency Converter by Tshepo");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(lightBlue);

        JLabel heading = new JLabel("Tshepo's Currency Converter", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(Color.DARK_GRAY);
        mainPanel.add(heading, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(lightBlue);

        inputPanel.add(new JLabel("Amount:"));
        amount = new JTextField();
        amount.setBackground(Color.WHITE);
        inputPanel.add(amount);

        inputPanel.add(new JLabel("From Currency:"));
        from = new JComboBox<>(getCurrencies());
        from.setBackground(Color.WHITE);
        inputPanel.add(from);
 


 
        inputPanel.add(new JLabel("To Currency:"));
        to = new JComboBox<>(getCurrencies());
        to.setBackground(Color.WHITE);
        inputPanel.add(to);

        swap = new JButton("Swap");
        swap.setBackground(new Color(135, 206, 250));  //Light Sky Blue
        swap.setForeground(Color.BLACK);
        swap.addActionListener(this);
        inputPanel.add(swap);

        submit = new JButton("Convert");
        submit.setBackground(new Color(135, 206, 250)); //Light Sky Blue
        submit.setForeground(Color.BLACK);
        submit.addActionListener(this);
        inputPanel.add(submit);
        
         

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        resultLabel = new JLabel(" ", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(Color.BLUE);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }



    /**
     
     *Returns a list of supported currencies.
     
     *@return An array of currency codes.
     */
    private String[] getCurrencies() {
        return new String[]{"ZAR", "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD"};
    }



    /**
     * Makes an API call to convert the given amount from the base currency to the target currency.
     *
     *@param base    The currency to convert from.
     *@param target  The currency to convert to.
     *@param amount  The amount to convert. 
     *@return The converted amount,or -1 if there was an error.     
     
     */
    public double convert(String base, String target, double amount) {
        try {
            String apiKey = "d679ac74b41e31cda85f8bcd"; //API key
            String urlStr = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + base;

            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder json = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            ExchangeRates rates = gson.fromJson(json.toString(), ExchangeRates.class);

            Double rate = rates.conversion_rates.get(target);

            if (rate != null) {
                return amount * rate;
            } else {
                System.out.println("⚠️ Invalid target currency or not available.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }

        return -1;
    }




   /**
    * Handles button click events:swapping currencies or performing the conversion.
    *@param e The ActionEvent triggered by a button click.
    
    */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            try {
                String base = (String) from.getSelectedItem();
                String target = (String) to.getSelectedItem();
                double enteredAmount = Double.parseDouble(amount.getText());

                double result = convert(base, target, enteredAmount);
                String stringResult = String.format("%.2f", result);
                resultLabel.setText(base + " " + enteredAmount + " = " + target + " " + stringResult);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
            }
        }

        if (e.getSource() == swap) {
            int fromIndex = from.getSelectedIndex();
            int toIndex = to.getSelectedIndex();
            from.setSelectedIndex(toIndex);
            to.setSelectedIndex(fromIndex);
        }
    }
    
    
    
    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments (not used).
     */

    public static void main(String[] args) {
        new CurrencyConverter();
    }
}
