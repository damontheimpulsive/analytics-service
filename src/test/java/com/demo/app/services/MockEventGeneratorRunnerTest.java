package com.demo.app.services;

import com.demo.app.contracts.EventIngestionService;
import com.demo.app.contracts.MockEventGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockEventGeneratorRunnerTest {

    @Mock
    private EventIngestionService eventIngestionService;

    @Mock
    private MockEventGenerator mockEventGenerator;

    @Test
    void startGenerator_shouldCreateAndStartGenerator() {
        // Arrange
        MockEventGeneratorRunner runner = spy(new MockEventGeneratorRunner(eventIngestionService));
        // When createMockEventGenerator is called, return our mock instead
        doReturn(mockEventGenerator).when(runner).createMockEventGenerator();

        // Act
        runner.startGenerator();

        // Assert
        verify(mockEventGenerator, times(1)).start();
    }
}