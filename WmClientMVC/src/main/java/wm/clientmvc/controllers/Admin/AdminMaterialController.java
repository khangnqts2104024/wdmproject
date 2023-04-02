package wm.clientmvc.controllers.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.PathParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wm.clientmvc.DTO.MaterialDTO;
import wm.clientmvc.DTO.OrderDTO;
import wm.clientmvc.securities.UserDetails.CustomUserDetails;
import wm.clientmvc.utils.APIHelper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wm.clientmvc.utils.Static_Status.orderStatusConfirm;

@Controller
@RequestMapping("/staff/materials")
public class AdminMaterialController {

    @RequestMapping
    public String materialIndex(Model model,@ModelAttribute("alertMessage") String alertMessage)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now =  LocalDateTime.now();
        String today=now.format(formatter);
        model.addAttribute("today",today);

        if (!alertMessage.isEmpty()) {
            model.addAttribute("alertMessage", alertMessage);
        }
        else {
            model.addAttribute("alertMessage", null);
        }
        return "adminTemplate/pages/organize/material-index";
    }

@RequestMapping(value="/detail/{id}",method = RequestMethod.GET)
public String showMaterialbyOrder(Model model, @PathVariable Integer id, @CookieValue(name="token",defaultValue = "")String token, HttpServletRequest request, HttpServletResponse response)
{
    //get order
    List<OrderDTO>list= new ArrayList<>();

//    ParameterizedTypeReference orderResponseType= new ParameterizedTypeReference() {};

    try {
        String oUrl="http://localhost:8080/api/orders/"+id;
              OrderDTO order=   APIHelper.makeApiCall(
                oUrl,
                HttpMethod.GET,
                null,
                token,
                OrderDTO.class,request,response
             );
             list.add(order);


    //get material
    ParameterizedTypeReference<List<MaterialDTO>> responseType=new ParameterizedTypeReference<List<MaterialDTO>>() {};
    String url="http://localhost:8080/api/materials/byorder/"+id;

        List<MaterialDTO> materialList= APIHelper.makeApiCall(
            url,
                HttpMethod.GET,
                null,
                token,
                responseType,request,response
        );
        //getday
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now =  LocalDateTime.now();
        String today=now.format(formatter);
        if(materialList!=null){
            for (MaterialDTO material:materialList)
            {
                material.setCount(material.getCount()*order.getTableAmount());
            }}

//        totalMaterial;
        model.addAttribute("today",today);
        model.addAttribute("orderList",list);
        model.addAttribute("materials",materialList);
        return "adminTemplate/pages/organize/material";
        } catch (Exception e) {
            model.addAttribute("message",e.getMessage());
            return "adminTemplate/error";
        }
    }

    @RequestMapping(value="/detail/searchId",method = RequestMethod.POST)
    public String materialSearchId(Model model,@PathParam("orderId")Integer orderId, @CookieValue(name="token",defaultValue = "")String token,RedirectAttributes redirectAttributes,HttpServletRequest request, HttpServletResponse response)
    {
//check team

        //
        //get order
        List<OrderDTO>list= new ArrayList<>();

//    ParameterizedTypeReference orderResponseType= new ParameterizedTypeReference() {};

        try {

                String oUrl="http://localhost:8080/api/orders/"+orderId;
                OrderDTO order=   APIHelper.makeApiCall(
                        oUrl,
                        HttpMethod.GET,
                        null,
                        token,
                        OrderDTO.class,request,response
                );
                list.add(order);
            //get material


            ParameterizedTypeReference<List<MaterialDTO>> responseType=new ParameterizedTypeReference<List<MaterialDTO>>() {};
            String url="http://localhost:8080/api/materials/byorder/"+orderId;

            List<MaterialDTO>   materialList= APIHelper.makeApiCall(
                    url,
                    HttpMethod.GET,
                    null,
                    token,
                    responseType,request,response
            );



            //getday
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now =  LocalDateTime.now();
            String today=now.format(formatter);
            if(materialList!=null){
            for (MaterialDTO material:materialList)
            {
             material.setCount(material.getCount()*order.getTableAmount());
            }
            }else {
                redirectAttributes.addFlashAttribute("alertMessage", "Cant found material!Try Again! ");
                return "redirect:/staff/materials";
            }

//        totalMaterial;
            model.addAttribute("today",today);
            model.addAttribute("orderList",list);
            model.addAttribute("materials",materialList);
            return "adminTemplate/pages/organize/material";


        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("alertMessage", "Cant found material!Try Again! ");
            return "redirect:/staff/materials";
        }
    }

    @RequestMapping(value="/detail/searchDate",method = RequestMethod.POST)
    public String materialSearchDate(Model model, @PathParam("date")String date, @CookieValue(name="token",defaultValue = "")String token, RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response)
    {
        //check team

        //get order
        try {

            //getday
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now =  LocalDateTime.now();
            String today=now.format(formatter);
            //get empId
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String role=authentication.getAuthorities().stream().findFirst().toString();

            //role admin
            if(role.contains("ADMIN")) {
                ParameterizedTypeReference<List<OrderDTO>> orderResponseType = new ParameterizedTypeReference<>() {};
                //confirm only
                String ourl = "http://localhost:8080/api/orders/byStatus/confirm";

             List<OrderDTO> list=getOrderList(token,ourl,date,request,response);

             if(list==null || list.size()==0)
             {
                 redirectAttributes.addFlashAttribute("alertMessage", "No confirm order found in this day! ");
                 return "redirect:/staff/materials";
             }

                //get material in day with count
          List<MaterialDTO> materialList=getMaterialList(token,list,request,response);

                model.addAttribute("today",today);
                model.addAttribute("orderList",list);
                model.addAttribute("materials",materialList);
                return "adminTemplate/pages/organize/material";
                //role ORGANIZE
            }else if(role.contains("ORGANIZE")){
                CustomUserDetails empUserDetails= (CustomUserDetails) authentication.getPrincipal();
                Long empId= empUserDetails.getUserId();

                //confirm only
                String ourl = "http://localhost:8080/api/orders/byTeam/empId/"+empId;
//
//get list of today
                List<OrderDTO>list=getOrderList(token,ourl,date,request,response);

                if(list==null || list.size()==0)
                {
                    redirectAttributes.addFlashAttribute("alertMessage", "No confirm order found in this day! ");
                    return "redirect:/staff/materials";
                }

                List<MaterialDTO> materialList=getMaterialList(token,list,request,response);

//        totalMaterial;
                model.addAttribute("today",today);
                model.addAttribute("orderList",list);
                model.addAttribute("materials",materialList);
                return "adminTemplate/pages/organize/material";
            }
           else{
                redirectAttributes.addFlashAttribute("alertMessage", "You not allow to Access this action! ");
                return "redirect:/staff/materials";

            }


        } catch (Exception e) {
            model.addAttribute("message",e.getMessage());
            return "adminTemplate/error";
        }
    }

    public List<OrderDTO> getOrderList(String token,String url,String date,HttpServletRequest request,HttpServletResponse response) throws IOException {

        List<OrderDTO>list= new ArrayList<>();
        ParameterizedTypeReference<List<OrderDTO>> orderResponseType = new ParameterizedTypeReference<>() {};
        List<OrderDTO> orderList = APIHelper.makeApiCall(
                url,
                HttpMethod.GET,
                null,
                token,
                orderResponseType,request,response);

        //getlist in day
        if(orderList!=null){
        for (OrderDTO order: orderList)
        {
            if(order.getOrderStatus().equalsIgnoreCase(orderStatusConfirm)&& order.getTimeHappen().contains(date))
            {
                list.add(order);
            }
        }
        }
        return list;
    }

    public List<MaterialDTO> getMaterialList(String token,List<OrderDTO>list,HttpServletRequest request,HttpServletResponse response) throws Exception {
        List<MaterialDTO> materialList=new ArrayList<>();

        for (OrderDTO order:list)
        {
            ParameterizedTypeReference<List<MaterialDTO>> responseType=new ParameterizedTypeReference<List<MaterialDTO>>() {};
            String url="http://localhost:8080/api/materials/byorder/"+order.getId();

            List<MaterialDTO> materials= APIHelper.makeApiCall(
                    url,
                    HttpMethod.GET,
                    null,
                    token,
                    responseType,request,response
            );

            Integer table=order.getTableAmount();

            //loop a new list
            for (MaterialDTO material : materials)
            {
                boolean materialExist = false;
                //time with table to get a new material count
                material.setCount(material.getCount()*table);

                for (MaterialDTO mate : materialList) {

                    //exit?
                    if (material.getMaterialCode().equalsIgnoreCase(mate.getMaterialCode())) {
                        //change unit if ext
                        mate.setCount(material.getCount() + mate.getCount());
                        materialExist = true;
                        break;
                    }
                }
                if(!materialExist)
                {
                    materialList.add(material);
                }
            }
        }
        return materialList;
    }


}
