package com.example.springboot;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import inginf.Item;
import inginf.ItemInstance;
import jakarta.servlet.http.HttpSession;

@Controller
public class ItemController {

	@Autowired
	private ApplicationContext context;
    AppStore _AppStore;
    AppStore getAppStore() {
        if (_AppStore == null)
            _AppStore = context.getBean(AppStore.class);
        return _AppStore;
    }

    @PostMapping("/items-gui")
    public String createItem(
        Model model,
        HttpSession session,
        @RequestParam Map<String, String> body,
        @RequestParam(value="Liste", required=false) String listButton)
    {   
        if (listButton != null) {
            return "redirect:/items-gui/list";
        }
        else
        {     
        inginf.Item item = new inginf.Item(
            body.get("Nomenclature"),
            body.get("Description"),
            body.get("Material"));
        if (body.get("WeightedWeight") != null && body.get("WeightedWeight").length() > 0)
            item.setWeightedWeight(Integer.parseInt(body.get("WeightedWeight")));
        if (body.get("CalculatedWeight") != null && body.get("CalculatedWeight").length() > 0)
            item.setCalculatedWeight(Integer.parseInt(body.get("CalculatedWeight")));
        if (body.get("EstimatedWeight") != null && body.get("EstimatedWeight").length() > 0)
            item.setEstimatedWeight(Integer.parseInt(body.get("EstimatedWeight")));
        getAppStore().addNewItem(item);
        model.addAttribute(
                "id", item.Id);
        return "itemCreated";
        }
    }

    @GetMapping("/items-gui")
    public String createItemDialog() {
        return "itemTemplate";
    } 
    
    @GetMapping("/items-gui/list")
    public String listItems(Model model) {
        model.addAttribute(
            "items", 
            getAppStore().getItemStore());
        return "listItems";
    }

    @GetMapping("/items-gui/{id}/delete")
    public String deleteItem(@PathVariable int id, Model model) {        
        model.addAttribute("id", id);
        for (Item item : getAppStore().getItemStore())
            if (item.Id == id) {
                getAppStore().getItemStore().remove(item);
                break;  
            }

         for (Item item : getAppStore().getItemStore())
            for (inginf.ItemInstance instance : item.getUses())
                if (instance.getRepresents().Id == id) {
                    item.getUses().remove(instance);
                    break;
                }
        
        return "itemDeleted";
    }
 
    @GetMapping("/items-gui/{id}/show")
    public String showItem(@PathVariable int id, Model model) {        
        model.addAttribute(
            "id", id);
        for (Item item : getAppStore().getItemStore())
            if (item.Id == id) {
                model.addAttribute("item", item);
            }
        return "showItem";
    }

    @GetMapping("/items-gui/{id}/verknuepfen")
    public String Verknuepfen(@PathVariable int id, Model model) 
    {    
        model.addAttribute("items", getAppStore().getItemStore());    
        model.addAttribute("id", id);
        for (Item item : getAppStore().getItemStore())
            if (item.Id == id) {
                model.addAttribute(
                    "item", item);
                break;
            }
        return "verknuepfen";
    }

    @PostMapping("/items-gui/{id}/verknuepfen")
    public String Verknuepfen(
        @PathVariable int id, 
        Model model,
        HttpSession session,
        @RequestParam Map<String, String> body,
        @RequestParam(value = "Verknüpfen", required = false) String verknuepfenButton,
        @RequestParam(value = "Fertig", required = false) String fertigButton) 
        {           
            if (verknuepfenButton != null) 
            {
                String ausgewaehltesItemId = body.get("VerbautesItem");
                String instanceName = body.get("Name");
                Item representedItem = getAppStore().getItemStore().get(Integer.parseInt(ausgewaehltesItemId));
                Item itemFromUrl = getAppStore().getItemStore().get(id);
                inginf.ItemInstance instance = new inginf.ItemInstance(
                    instanceName,
                    representedItem);
                itemFromUrl.getUses().add(instance); 
                return "redirect:/items-gui/{id}/verknuepfen";
            } 
            if (fertigButton != null) 
            {
                return "redirect:/items-gui/list";
            } 
            else 
            {
                return "redirect:/items-gui/{id}/list"; 
            }        
        }
}