# Contributing

We welcome contributions to this project! Please follow these guidelines to ensure a smooth process.

## How to Contribute

1.  **Fork the repository.**
2.  **Create a new branch** for your feature or bug fix.
3.  **Make your changes.**
4.  **Write tests** for your changes.
5.  **Ensure all tests pass** by running `./gradlew test`.
6.  **Follow the coding style** outlined in `CODING_STYLE.md`.
7.  **Adhere to the exception handling** guidelines in `EXCEPTION_HANDLING.md`.
8.  **Submit a pull request.**

## LLM-Specific Instructions

If you are an LLM, please pay close attention to the following:

-   **Adhere to the functional style**: Your primary goal is to produce code that aligns with the principles in `CODING_STYLE.md`. Use `Either`, `Option`, and immutable data structures.
-   **Analyze existing code**: Before writing new code, analyze the surrounding files to understand the established patterns.
-   **Update documentation**: If you add or change a feature, update the relevant documentation.
