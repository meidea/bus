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
package org.aoju.bus.image.plugin;

import org.aoju.bus.core.utils.IoUtils;
import org.aoju.bus.image.*;
import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.galaxy.io.DicomOutputStream;
import org.aoju.bus.image.metric.ApplicationEntity;
import org.aoju.bus.image.metric.Association;
import org.aoju.bus.image.metric.Commands;
import org.aoju.bus.image.metric.Connection;
import org.aoju.bus.image.metric.pdu.PresentationContext;
import org.aoju.bus.image.metric.service.*;
import org.aoju.bus.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Kimi Liu
 * @version 5.8.8
 * @since JDK 1.8+
 */
public class IanSCP extends Device {

    private final ApplicationEntity ae = new ApplicationEntity("*");
    private final Connection conn = new Connection();
    private File storageDir;
    private int status;

    private final DicomService ianSCP =
            new AbstractDicomService(UID.InstanceAvailabilityNotificationSOPClass) {

                @Override
                public void onDimseRQ(Association as, PresentationContext pc,
                                      Dimse dimse, Attributes cmd, Attributes data)
                        throws IOException {
                    if (dimse != Dimse.N_CREATE_RQ)
                        throw new DicomServiceException(Status.UnrecognizedOperation);
                    Attributes rsp = Commands.mkNCreateRSP(cmd, status);
                    Attributes rspAttrs = IanSCP.this.create(as, cmd, data);
                    as.tryWriteDimseRSP(pc, rsp, rspAttrs);
                }
            };

    public IanSCP() throws IOException {
        super("ianscp");
        addConnection(conn);
        addApplicationEntity(ae);
        ae.setAssociationAcceptor(true);
        ae.addConnection(conn);
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        serviceRegistry.addDicomService(ianSCP);
        ae.setDimseRQHandler(serviceRegistry);
    }

    public File getStorageDirectory() {
        return storageDir;
    }

    public void setStorageDirectory(File storageDir) {
        if (storageDir != null)
            storageDir.mkdirs();
        this.storageDir = storageDir;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private Attributes create(Association as, Attributes rq, Attributes rqAttrs)
            throws DicomServiceException {
        if (storageDir == null)
            return null;
        String cuid = rq.getString(Tag.AffectedSOPClassUID);
        String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
        File file = new File(storageDir, iuid);
        if (file.exists())
            throw new DicomServiceException(Status.DuplicateSOPinstance).
                    setUID(Tag.AffectedSOPInstanceUID, iuid);
        DicomOutputStream out = null;
        Logger.info("{}: M-WRITE {}", as, file);
        try {
            out = new DicomOutputStream(file);
            out.writeDataset(
                    Attributes.createFileMetaInformation(iuid, cuid,
                            UID.ExplicitVRLittleEndian),
                    rqAttrs);
        } catch (IOException e) {
            Logger.warn(as + ": Failed to store Instance Available Notification:", e);
            throw new DicomServiceException(Status.ProcessingFailure, e);
        } finally {
            IoUtils.close(out);
        }
        return null;
    }

}