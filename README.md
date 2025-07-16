E-Wallet

Our E-Wallet is a simple, secure way to manage your money online. You can easily create an account to send and receive money anytime, anywhere.
It consist of four microservices that work together flawlessly, offering a simple, yet impressive digital wallet experience.

Microservices Architecture
User Service: This service acts as the entry point for users into our E-Wallet application. Users can register for an account, enabling them to send and receive money effortlessly. Additionally, this service provides users with the ability to view their account details.

Wallet Service: Upon account creation via the User Service, the Wallet Service automatically generates a wallet for the new user. This service also credits a new user bonus to the wallet, encouraging user engagement from the outset.

Transaction Service: The Transaction Service manages the flow of funds between users. When a transaction is initiated, this service coordinates with the Wallet Service to accurately update the balance of both the sender's and receiver's wallets, depending on the transaction's outcome.

Notification Service: This service ensures that both parties involved in a transaction are kept informed about the transaction's status. By interfacing with the Notification Service, users receive timely updates, enhancing user experience and satisfaction.

Kafka Integration: All microservices communicate through Kafka, which acts as the nervous system of our application. By producing and consuming messages within Kafka clusters, our microservices can perform their respective functions efficiently and reliably, ensuring seamless operations.

Security: Users and administrators are assigned different roles and permissions, ensuring that sensitive operations are protected and accessible only to those with the appropriate authority.
