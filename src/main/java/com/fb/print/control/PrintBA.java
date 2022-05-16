package com.fb.print.control;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import com.fb.StringUtils;
import com.fb.print.entity.Address;
import com.fb.print.entity.Data;
import com.fb.print.entity.Image;
import com.fb.print.entity.Multimedia;
import com.fb.print.entity.Person;
import com.fb.print.entity.RtfReferenceEnum;

/**
 * Business Activity for Print
 */
@ApplicationScoped
public class PrintBA {

    private static final MediaType MEDIATYPE_IMAGE_PNG = new MediaType("image", "png");

    @Inject
    PrintESI printESI;

    @ConfigProperty(name = "http.proxyHost")
    String proxyHost;

    @ConfigProperty(name = "http.proxyPort")
    String proxyPort;

    @ConfigProperty(name = "cib.print.ducument.guid")
    String documentGuid;

    /**
     * Print document
     *
     * @return PDF stream
     */
    public Response printDocument() {

        Person homer = new Person();
        homer.setName("Homer Simpson");
        homer.setAge(42);
        Address address = new Address();
        address.setPostBox("PO3242");
        address.setStreet("Spring Street 245");
        address.setCity("435 Springfield, NY");
        homer.setAddress(address);

        Multimedia multimedia = new Multimedia();
        Image faceFront = new Image();
        faceFront.setIdentifier(UUID.randomUUID().toString());
        faceFront.setRtfReference(RtfReferenceEnum.FACE_FRONT);

        Image faceProfile = new Image();
        faceProfile.setIdentifier(UUID.randomUUID().toString());
        faceProfile.setRtfReference(RtfReferenceEnum.FACE_PROFILE);

        Image house = new Image();
        house.setIdentifier(UUID.randomUUID().toString());
        house.setRtfReference(RtfReferenceEnum.HOUSE);

        List<Image> faces = new ArrayList<>(2);
        faces.add(faceFront);
        faces.add(faceProfile);

        multimedia.setFaces(faces);
        multimedia.setHouse(house);

        Data data = new Data();
        data.setPerson(homer);
        data.setMultimedia(multimedia);

        Map<String, String> mergeOptions = new HashMap<>();
        mergeOptions.put("--datafile", "/root/multi");
        mergeOptions.put("--logfile", "merge.log");
        mergeOptions.put("--prefix-delimiter", ".");


        MultipartFormDataOutput multipartFormDataOutput = new MultipartFormDataOutput();
        multipartFormDataOutput.addFormData("data", data, MediaType.APPLICATION_JSON_TYPE, "data.json");
        multipartFormDataOutput.addFormData("merge", mergeOptions, MediaType.APPLICATION_JSON_TYPE, "merge.json");

        Proxy proxy = Proxy.NO_PROXY;
        if(!StringUtils.isBlank(proxyHost) && !StringUtils.isBlank(proxyPort)) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
        }

        try {
            URL faceFrontUrl = new URL("https://www.kindpng.com/picc/m/144-1448183_homer-simpson-face-render-png-by-8scorpion-d6mtcmo.png");
            URL faceProfileUrl = new URL("https://www.kindpng.com/picc/m/144-1448383_best-free-simpsons-png-image-without-background-homer.png");
            URL houseUrl = new URL("https://www.kindpng.com/picc/m/327-3272173_simpsons-tapped-out-hd-png-download.png");

            multipartFormDataOutput.addFormData("resources", faceFrontUrl.openConnection(proxy).getInputStream(),
                    MEDIATYPE_IMAGE_PNG,
                    faceFront.getIdentifier());
            multipartFormDataOutput.addFormData("resources", faceProfileUrl.openConnection(proxy).getInputStream(), MEDIATYPE_IMAGE_PNG,
                    faceProfile.getIdentifier());
            multipartFormDataOutput.addFormData("resources", houseUrl.openConnection(proxy).getInputStream(), MEDIATYPE_IMAGE_PNG,
                    house.getIdentifier());
        }catch (IOException e) {
            e.printStackTrace();
        }

        return printESI.printDocument(documentGuid, multipartFormDataOutput, "0001", "TO");
    }

}
