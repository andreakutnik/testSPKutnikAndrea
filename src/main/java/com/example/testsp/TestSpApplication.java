package com.example.testsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TestSpApplication {

	// Strategy interface
	public interface NumeStrategy {
		String getNume(Component component);
	}

	// Concrete strategy for normal names
	public class NumeNormalStrategy implements NumeStrategy {
		@Override
		public String getNume(Component component) {
			return component.getNume();
		}
	}

	// Concrete strategy for uppercase names
	public class NumeUppercaseStrategy implements NumeStrategy {
		@Override
		public String getNume(Component component) {
			return component.getNume().toUpperCase();
		}
	}

	// Interfața pentru componente
	public interface Component {
		String getNume();
	}

	// Clasa pentru obiectele individuale
	public class Creatura implements Component {
		private String nume;

		public Creatura(String nume) {
			this.nume = nume;
		}

		@Override
		public String getNume() {
			return nume;
		}
	}

	// Clasa pentru compoziții
	public class Batalion implements Component {
		private String nume;
		private List<Component> componente;

		public Batalion(String nume) {
			this.nume = nume;
			this.componente = new ArrayList<>();
		}

		public void adaugaComponenta(Component componenta) {
			componente.add(componenta);
		}

		@Override
		public String getNume() {
			return nume;
		}

		public List<Component> getComponente() {
			return componente;
		}
	}

	// Proxy pentru Component
	public class ComponentProxy implements Component {
		private Component componentReal;
		private NumeStrategy numeStrategy;

		public ComponentProxy(Component componentReal, NumeStrategy numeStrategy) {
			this.componentReal = componentReal;
			this.numeStrategy = numeStrategy;
		}

		@Override
		public String getNume() {
			return numeStrategy.getNume(componentReal);
		}
	}

	@RestController
	@RequestMapping("/armata")
	public class ArmataController {

		@Autowired
		private TestSpApplication application;

		@GetMapping("/detalii")
		public List<String> getDetaliiArmata() {
			List<String> detaliiArmata = new ArrayList<>();

			List<Component> armata = application.creareArmata();

			// Use NumeNormalStrategy by default
			NumeStrategy numeStrategy = new NumeNormalStrategy();

			for (Component component : armata) {
				detaliiArmata.add(afiseazaDetalii(component, numeStrategy));
			}

			return detaliiArmata;
		}

		private String afiseazaDetalii(Component component, NumeStrategy numeStrategy) {
			StringBuilder detalii = new StringBuilder();
			detalii.append("Nume: ").append(numeStrategy.getNume(component)).append(" ");

			if (component instanceof Batalion) {
				detalii.append("Componente: ");
				List<Component> componenteBatalion = ((Batalion) component).getComponente();
				for (int i = 0; i < componenteBatalion.size(); i++) {
					detalii.append(afiseazaDetalii(componenteBatalion.get(i), numeStrategy));
					if (i < componenteBatalion.size() - 1) {
						detalii.append(", ");
					}
				}
			}

			return detalii.toString();
		}
	}

	public List<Component> creareArmata() {
		List<Component> armata = new ArrayList<>();

		Batalion batalionElfi = new Batalion("Batalionul de Elfi");
		batalionElfi.adaugaComponenta(new Creatura("Legolas"));
		batalionElfi.adaugaComponenta(new Creatura("Thranduil"));
		armata.add(batalionElfi);

		Batalion batalionGnomi = new Batalion("Batalionul de Gnomi");
		batalionGnomi.adaugaComponenta(new Creatura("Gimli"));
		batalionGnomi.adaugaComponenta(new Creatura("Balin"));
		armata.add(batalionGnomi);

		Batalion batalionEnți = new Batalion("Batalionul de Enți");
		batalionEnți.adaugaComponenta(new Creatura("Treebeard"));
		batalionEnți.adaugaComponenta(new Creatura("Quickbeam"));
		armata.add(batalionEnți);

		return armata;
	}

	public static void main(String[] args) {
		SpringApplication.run(TestSpApplication.class, args);
	}
}
