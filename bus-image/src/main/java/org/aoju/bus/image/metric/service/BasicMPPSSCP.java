/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.image.metric.service;

import org.aoju.bus.image.Dimse;
import org.aoju.bus.image.Status;
import org.aoju.bus.image.UID;
import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.metric.Association;
import org.aoju.bus.image.metric.Commands;
import org.aoju.bus.image.metric.pdu.PresentationContext;

import java.io.IOException;

/**
 * @author Kimi Liu
 * @version 5.8.8
 * @since JDK 1.8+
 */
public class BasicMPPSSCP extends AbstractDicomService {

    public BasicMPPSSCP() {
        super(UID.ModalityPerformedProcedureStepSOPClass);
    }

    public static void mayNoLongerBeUpdated() throws DicomServiceException {
        throw new DicomServiceException(Status.ProcessingFailure,
                "Performed Procedure Step Object may no longer be updated")
                .setErrorID(0xA710);
    }

    @Override
    public void onDimseRQ(Association as, PresentationContext pc, Dimse dimse,
                          Attributes rq, Attributes rqAttrs) throws IOException {
        switch (dimse) {
            case N_CREATE_RQ:
                onNCreateRQ(as, pc, rq, rqAttrs);
                break;
            case N_SET_RQ:
                onNSetRQ(as, pc, rq, rqAttrs);
                break;
            default:
                throw new DicomServiceException(Status.UnrecognizedOperation);
        }
    }

    protected void onNCreateRQ(Association as, PresentationContext pc,
                               Attributes rq, Attributes rqAttrs) throws IOException {
        Attributes rsp = Commands.mkNCreateRSP(rq, Status.Success);
        Attributes rspAttrs = create(as, rq, rqAttrs, rsp);
        as.tryWriteDimseRSP(pc, rsp, rspAttrs);
    }

    protected Attributes create(Association as, Attributes rq,
                                Attributes rqAttrs, Attributes rsp) throws DicomServiceException {
        return null;
    }

    protected void onNSetRQ(Association as, PresentationContext pc,
                            Attributes rq, Attributes rqAttrs) throws IOException {
        Attributes rsp = Commands.mkNSetRSP(rq, Status.Success);
        Attributes rspAttrs = set(as, rq, rqAttrs, rsp);
        as.tryWriteDimseRSP(pc, rsp, rspAttrs);
    }

    protected Attributes set(Association as, Attributes rq, Attributes rqAttrs,
                             Attributes rsp) throws DicomServiceException {
        return null;
    }

}