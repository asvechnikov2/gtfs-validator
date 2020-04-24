/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.RawFileRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ValidateAllRequiredFilePresenceTest {

    @Test
    void allRequiredPresentShouldNotGenerateNotice() {

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        Set<String> testSet = Set.of("req0.req","req1.req","req2.req","req3.req","req4.req",
                                        "req5.req","req6.req","req7.req","req8.req","req9.req",
                                        "opt0.opt","opt1.opt","opt2.opt","opt3.opt","opt4.opt",
                                        "opt5.opt","opt6.opt","opt7.opt","opt8.opt","opt9.opt");
        when(mockFileRepo.getFilenameAll()).thenReturn(testSet);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        List<String> testRequiredList = List.of("req0.req","req1.req","req2.req","req3.req","req4.req",
                                                "req5.req","req6.req","req7.req","req8.req","req9.req");
        when(mockSpecRepo.getRequiredFilenameList()).thenReturn(testRequiredList);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateAllRequiredFilePresence underTest = new ValidateAllRequiredFilePresence(
                mockSpecRepo,
                mockFileRepo,
                mockResultRepo
        );

        List<String> result = underTest.execute();
        assertEquals(10, result.size());
        assertEquals(List.of("req0.req", "req1.req", "req2.req", "req3.req", "req4.req", "req5.req", "req6.req",
                "req7.req", "req8.req", "req9.req"), result);

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockFileRepo, times(1)).getFilenameAll();
        inOrder.verify(mockSpecRepo, times(2)).getRequiredFilenameList();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

    @Test
    void missingRequiredShouldGenerateOneNoticePerMissingFile() {

        RawFileRepository mockFileRepo = mock(RawFileRepository.class);
        Set<String> testSet = Set.of("req0.req","req1.req","req2.req","req3.req","req4.req",
                                        "req5.req","req6.req","req7.req","req8.req","req9.req",
                                        "opt0.opt","opt1.opt","opt2.opt","opt3.opt","opt4.opt",
                                        "opt5.opt","opt6.opt","opt7.opt","opt8.opt","opt9.opt");
        when(mockFileRepo.getFilenameAll()).thenReturn(testSet);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        List<String> testRequiredList = List.of("req0.req","req1.req","req2.req","req3.req","req4.req",
                                                "req5.req","req6.req","req7.req","req8.req","req9.req",
                                                "req10.req","req11.req","req12.req","req13.req","req14.req");
        when(mockSpecRepo.getRequiredFilenameList()).thenReturn(testRequiredList);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateAllRequiredFilePresence underTest = new ValidateAllRequiredFilePresence(
                mockSpecRepo,
                mockFileRepo,
                mockResultRepo
        );

        List<String> result = underTest.execute();
        assertEquals(15, result.size());

        InOrder inOrder = Mockito.inOrder(mockFileRepo, mockSpecRepo);

        inOrder.verify(mockFileRepo, times(1)).getFilenameAll();
        inOrder.verify(mockSpecRepo, times(2)).getRequiredFilenameList();
        inOrder.verify(mockFileRepo, times(15)).getFilenameAll();
        inOrder.verify(mockSpecRepo, times(1)).getRequiredFilenameList();
        verify(mockResultRepo, times(5)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockFileRepo, mockSpecRepo, mockResultRepo);
    }

}